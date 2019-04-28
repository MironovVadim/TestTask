package com.example.orders.service.impl;

import com.example.orders.exception.NoOrderException;
import com.example.orders.exception.NotEnoughItemsException;
import com.example.orders.model.Orders;
import com.example.orders.model.dto.OrderDto;
import com.example.orders.model.dto.ReservedItemDto;
import com.example.orders.repository.OrderRepository;
import com.example.orders.repository.ReservedItemRepository;
import com.example.orders.service.OrderService;
import com.example.orders.warehouseapi.WareHouseApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private ReservedItemRepository reservedItemRepository;
    private WareHouseApi wareHouseApi;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            ReservedItemRepository reservedItemRepository,
                            WareHouseApi wareHouseApi) {
        this.orderRepository = orderRepository;
        this.reservedItemRepository = reservedItemRepository;
        this.wareHouseApi = wareHouseApi;
    }

    @Override
    public List<Orders> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Orders getOrderById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NoOrderException("Не найдено ни одного заказа с заданным Id"));
    }

    @Override
    public Orders createOrder(OrderDto orderDto) {
        Orders newOrder = orderDto.createOrderFromDto();
        orderRepository.save(newOrder);
        return this.orderItems(newOrder, orderDto);
    }

    @Override
    public void cancelOrder(int id) {
        orderRepository.deleteById(id);
        wareHouseApi.cancelOrderItems(id);
    }

    @Transactional
    public Orders orderItems(Orders newOrder, OrderDto orderDto) {
        int orderId = newOrder.getId();
        List<ReservedItemDto> reservedItems;
        try {
            List<ReservedItemDto> itemsToReserve = orderDto.getReservedItems();
            if (!wareHouseApi.isEnoughItemsToOrder(itemsToReserve)) {
                throw new NotEnoughItemsException("Недостаточно вещей на складе для заказа");
            }
            itemsToReserve.forEach(item -> item.setOrderId(orderId));
            reservedItems = wareHouseApi.orderItems(itemsToReserve);
        } catch (Exception e) {
            orderRepository.delete(newOrder);
            throw new RuntimeException(e);
        }

        try {
            double orderPrice = wareHouseApi.getOrderPrice(orderId);

            newOrder.extractAndSetReservedItemsIds(reservedItems);
            newOrder.setState(Orders.State.ACTIVE);
            newOrder.setPrice(orderPrice);

            reservedItemRepository.saveAll(newOrder.getReservedItems());
            return orderRepository.save(newOrder);
        } catch (Exception e) {
            this.cancelOrder(orderId);
            throw new RuntimeException(e);
        }
    }
}
