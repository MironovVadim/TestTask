package com.example.warehouse.storeapi;

import com.example.warehouse.exception.OrderStateException;
import com.example.warehouse.model.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для взаимодействия с сервисом Orders
 */
@Component
public class StoreApi {

    private StoreConfiguration configuration;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public StoreApi(StoreConfiguration configuration) {
        this.configuration = configuration;
    }

    public void checkOrderExists(int orderId) {
        Map<String, Integer> params = new HashMap<>();
        params.put("orderId", orderId);
        URI url = restTemplate
                .getUriTemplateHandler()
                .expand(configuration.getUrl() + "/orders/{orderId}", params);
        ResponseEntity<OrderDto> response = restTemplate.getForEntity(url, OrderDto.class);
        OrderDto order = response.getBody();
        // Если заказ не NEW, значит полученые данные некорректны
        if (!order.getState().equals("NEW")) {
            throw new OrderStateException("Заказ уже оформлен либо отменен");
        }
    }
}
