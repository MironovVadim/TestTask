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
        // Создание заказа со статусом NEW специально вынес из под основной транзакции,
        // чтобы у сервиса WareHouse была возможность проверить наличие существующего заказа.
        // Если случается исключение, то я просто выставляю заказу статус CANCELED без удаления.
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
        // Если этот блок выполняется не до конца, то в сервисе WareHouse не произойдет изменений
        // и можно будет только отметить заказ как отмененый.
        try {
            List<ReservedItemDto> itemsToReserve = orderDto.getReservedItems();
            // Проверка на наличие необходимых товаров
            if (!wareHouseApi.isEnoughItemsToOrder(itemsToReserve)) {
                throw new NotEnoughItemsException("Недостаточно вещей на складе для заказа");
            }
            itemsToReserve.forEach(item -> item.setOrderId(orderId));
            // обращение в WareHouse за оформлением Item'ов
            reservedItems = wareHouseApi.orderItems(itemsToReserve);
        } catch (Exception e) {
            orderRepository.delete(newOrder);
            throw new RuntimeException(e);
        }

        // Если в этом блоке упадет исключение, то вызов метода this.cancelOrder(orderId);
        // на 92 строчке должен откатить изменения в сервисе WareHouse (Item'ы будут отвечены как отмененные)
        try {
            // цену каждого Item'а можно было добавить в DTO, но сделал как сделал.
            double orderPrice = wareHouseApi.getOrderPrice(orderId);

            newOrder.extractAndSetReservedItemsIds(reservedItems);
            // Изменяю статус на ACTIVE в момент, когда Item'ы в WareHouse зарезервированы успешно
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
