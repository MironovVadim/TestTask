package com.example.warehouse.controller;

import com.example.warehouse.model.Item;
import com.example.warehouse.model.ReservedItem;
import com.example.warehouse.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.List;

@Transactional
@RestController
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> getAll() {
        return itemService.getAll();
    }

    @GetMapping("/all")
    public List<Item> getByIds(@RequestParam("id") List<Integer> itemIds) {
        return itemService.getByIds(itemIds);
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody Item item) {
        Item newItem = itemService.createItem(item);
        URI resourceLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newItem.getId())
                .toUri();
        return ResponseEntity
                .created(resourceLocation)
                .build();
    }

    @PostMapping("/isEnoughItemsToOrder")
    public boolean isEnoughItemsToOrder(@RequestBody List<ReservedItem> reservedItems) {
        return itemService.isEnoughItemsToOrder(reservedItems);
    }

    @PostMapping("/order")
    public List<ReservedItem> orderItems(@RequestBody List<ReservedItem> reservedItems) {
        return itemService.orderItems(reservedItems);
    }

    @GetMapping("/order/{orderId}/price")
    public double getOrderPrice(@PathVariable int orderId) {
        return itemService.getOrderPrice(orderId);
    }

    @PutMapping("/order/{orderId}/cancel")
    public void cancelItemOrder(@PathVariable int orderId) {
        itemService.cancelItemOrder(orderId);
    }


}
