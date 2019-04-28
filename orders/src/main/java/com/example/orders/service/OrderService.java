package com.example.orders.service;

import com.example.orders.model.Orders;
import com.example.orders.model.dto.OrderDto;

import java.util.List;

public interface OrderService {

    List<Orders> getAll();

    Orders getOrderById(int id);

    Orders createOrder(OrderDto orderDto);

    void cancelOrder(int orderId);
}
