package com.example.orders.controller;

import com.example.orders.model.Orders;
import com.example.orders.model.dto.OrderDto;
import com.example.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Orders> get() {
        return orderService.getAll();
    }

    @GetMapping("/{orderId}")
    public Orders getById(@PathVariable int orderId) {
        return orderService.getOrderById(orderId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody OrderDto orderDto) {
        Orders newOrders = orderService.createOrder(orderDto);
        URI resourceLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newOrders.getId())
                .toUri();
        return ResponseEntity
                .created(resourceLocation)
                .build();
    }

    @PutMapping("/{orderId}/cancel")
    public void cancelOrder(@PathVariable int orderId) {
        orderService.cancelOrder(orderId);
    }
}
