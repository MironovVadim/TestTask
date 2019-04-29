package com.example.warehouse.service.impl;

import com.example.warehouse.exception.ItemExistenceException;
import com.example.warehouse.exception.NotEnoughItemsException;
import com.example.warehouse.helper.ItemCountHelper;
import com.example.warehouse.model.Item;
import com.example.warehouse.model.ReservedItem;
import com.example.warehouse.repository.ItemRepository;
import com.example.warehouse.repository.ReservedItemRepository;
import com.example.warehouse.service.ItemService;
import com.example.warehouse.storeapi.StoreApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
    private ReservedItemRepository reservedItemRepository;
    private ItemCountHelper itemCountHelper;
    private StoreApi storeApi;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           ReservedItemRepository reservedItemRepository,
                           ItemCountHelper itemCountHelper,
                           StoreApi storeApi) {
        this.itemRepository = itemRepository;
        this.reservedItemRepository = reservedItemRepository;
        this.itemCountHelper = itemCountHelper;
        this.storeApi = storeApi;
    }

    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Override
    public List<ReservedItem> orderItems(List<ReservedItem> reservedItems) {
        // Проверяю на то, что все itemId существуют в БД и что нужное количество для заказа имеется
        this.checkReservedItemsOnErrors(reservedItems);
        List<Integer> ids = reservedItems
                .stream()
                .map(ReservedItem::getItemId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllById(ids);
        // Вычитаю необходимое количество из таблицы Item
        itemCountHelper.subtractItemCount(reservedItems, items);

        itemRepository.saveAll(items);
        // и сохраняю как зарезервированные в таблице ReservedItem
        reservedItemRepository.saveAll(reservedItems);
        return reservedItems;
    }

    /**
     * Проверяю, все ли itemId существующие
     * @param reservedItems
     * @return
     */
    private boolean isAllItemsExists(List<ReservedItem> reservedItems) {
        List<Integer> itemIds = reservedItems
                .stream()
                .map(ReservedItem::getItemId)
                .collect(Collectors.toList());
        int count = itemRepository.countByIdIn(itemIds);
        return count == itemIds.size();
    }

    /**
     * Отменить резерв Item'ов
     * @param orderId
     */
    @Override
    public void cancelItemOrder(int orderId) {
        List<ReservedItem> reservedItems = reservedItemRepository.findByOrderId(orderId);
        List<Item> items = this.findItemsByReservedItems(reservedItems);
        itemCountHelper.restoreItemCount(reservedItems, items);

        itemRepository.saveAll(items);
        reservedItemRepository.deleteAll(reservedItems);
    }

    /**
     * Хватает ли Item'ов для заказа
     * @param reservedItems
     * @return
     */
    @Override
    public boolean isEnoughItemsToOrder(List<ReservedItem> reservedItems) {
        List<Integer> countRemainders = this.getCountRemainders(reservedItems);
        // если хоть один остаток меньше нуля, значит заказ не будет осуществлен
        return countRemainders
                .stream()
                .noneMatch(remainder -> 0 > remainder);
    }

    /**
     * Посчитать стоимость заказа
     * @param orderId
     * @return
     */
    @Override
    public double getOrderPrice(int orderId) {
        storeApi.checkOrderExists(orderId);
        List<ReservedItem> reservedItems = reservedItemRepository.findByOrderId(orderId);
        List<Item> items = this.findItemsByReservedItems(reservedItems);
        return itemCountHelper.countOrderPrice(reservedItems, items);
    }

    @Override
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getByIds(List<Integer> itemIds) {
        return itemRepository.findAllById(itemIds);
    }

    private List<Item> findItemsByReservedItems(List<ReservedItem> reservedItems) {
        List<Integer> itemIds = reservedItems
                .stream()
                .map(ReservedItem::getItemId)
                .collect(Collectors.toList());
        return itemRepository.findAllById(itemIds);
    }

    /**
     * Узнать остаток Item'ов в случае заказа
     * @param reservedItems
     * @return
     */
    private List<Integer> getCountRemainders(List<ReservedItem> reservedItems) {
        Map<Integer, Integer> requiredItemIdCountMap = reservedItems
                .stream()
                .collect(
                        Collectors.toMap(ReservedItem::getItemId, ReservedItem::getCount)
                );
        Set<Integer> itemIds = requiredItemIdCountMap.keySet();
        List<Item> dbItems = itemRepository.findAllById(itemIds);
        return dbItems
                .stream()
                .map(dbItem -> {
                    int itemId = dbItem.getId();
                    Integer requiredAmount = requiredItemIdCountMap.get(itemId);
                    return dbItem.getAvailableCount() - requiredAmount;
                })
                .collect(Collectors.toList());
    }

    /**
     * Проверка на корректность данных по Item'ам
     * @param reservedItems
     */
    private void checkReservedItemsOnErrors(List<ReservedItem> reservedItems) {
        if (!this.isAllItemsExists(reservedItems)) {
            throw new ItemExistenceException("Не все указанные вещи существуют на складе");
        }
        if (!this.isEnoughItemsToOrder(reservedItems)) {
            throw new NotEnoughItemsException("Недостаточно вещей на складе для заказа");
        }

    }
}