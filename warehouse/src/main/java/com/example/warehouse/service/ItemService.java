package com.example.warehouse.service;

import com.example.warehouse.model.Item;
import com.example.warehouse.model.ReservedItem;

import java.util.List;

public interface ItemService {

    List<Item> getAll();

    List<ReservedItem> orderItems(List<ReservedItem> reservedItems);

    void cancelItemOrder(int orderId);

    boolean isEnoughItemsToOrder(List<ReservedItem> reservedItems);

    double getOrderPrice(int orderId);

    Item createItem(Item item);

    List<Item> getByIds(List<Integer> itemIds);
}
