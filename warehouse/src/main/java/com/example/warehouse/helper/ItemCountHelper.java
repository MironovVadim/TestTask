package com.example.warehouse.helper;

import com.example.warehouse.model.Item;
import com.example.warehouse.model.ReservedItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
public class ItemCountHelper {

    public double countOrderPrice(List<ReservedItem> reservedItems, List<Item> items) {
        List<Double> itemPrices = items
                .stream()
                .map(item -> {
                    ReservedItem foundItem = reservedItems
                            .stream()
                            .filter(reservedItem -> item.getId().equals(reservedItem.getItemId()))
                            .findFirst()
                            .get();

                    double itemPrice = item.getPrice();
                    int itemCount = foundItem.getCount();
                    return itemPrice * itemCount;
                })
                .collect(Collectors.toList());
        return itemPrices
                .stream()
                .reduce((price1, price2) -> price1 + price2)
                .get();
    }

    public void subtractItemCount(List<ReservedItem> reservedItems, List<Item> dbItems) {
        this.operateItemCount(reservedItems, dbItems, (a1, a2) -> a1 - a2);
    }

    public void restoreItemCount(List<ReservedItem> reservedItems, List<Item> dbItems) {
        this.operateItemCount(reservedItems, dbItems, (a1, a2) -> a1 + a2);
    }

    private void operateItemCount(List<ReservedItem> reservedItems, List<Item> dbItems, BiFunction<Integer, Integer, Integer> func) {
        dbItems.forEach(dbItem -> {
            int itemId = dbItem.getId();
            int itemAvailableCount = dbItem.getAvailableCount();
            int reservedItemCount = reservedItems
                    .stream()
                    .filter(reservedItem -> reservedItem.getItemId().equals(itemId))
                    .findFirst()
                    .get()
                    .getCount();

            dbItem.setAvailableCount(func.apply(itemAvailableCount, reservedItemCount));
        });
    }
}
