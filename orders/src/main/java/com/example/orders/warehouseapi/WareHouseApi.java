package com.example.orders.warehouseapi;

import com.example.orders.model.dto.ReservedItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WareHouseApi {

    private RestTemplate restTemplate = new RestTemplate();
    private WareHouseConfiguration configuration;

    @Autowired
    public WareHouseApi(WareHouseConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean isEnoughItemsToOrder(List<ReservedItemDto> reservedItems) {
        URI url = restTemplate
                .getUriTemplateHandler()
                .expand(configuration.getUrl() + "/items/isEnoughItemsToOrder");
        HttpEntity entity = new HttpEntity<>(reservedItems);
        ResponseEntity<Boolean> response = restTemplate.postForEntity(url, entity, Boolean.class);
        return response.getBody();
    }

    public List<ReservedItemDto> orderItems(List<ReservedItemDto> itemsToReserve) {
        URI url = restTemplate
                .getUriTemplateHandler()
                .expand(configuration.getUrl() + "/items/order");
        HttpEntity entity = new HttpEntity<>(itemsToReserve);
        ResponseEntity<List<ReservedItemDto>> response = restTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<List<ReservedItemDto>>(){});
        return response.getBody();
    }

    public void cancelOrderItems(int orderId) {
        Map<String, Integer> params = new HashMap<>();
        params.put("orderId", orderId);
        URI url = restTemplate
                .getUriTemplateHandler()
                .expand(configuration.getUrl() + "/items/order/{orderId}/cancel", params);
        restTemplate.exchange(url, HttpMethod.PUT, null, Void.class);
    }

    public double getOrderPrice(int orderId) {
        Map<String, Integer> params = new HashMap<>();
        params.put("orderId", orderId);
        URI url = restTemplate
                .getUriTemplateHandler()
                .expand(configuration.getUrl() + "/items/order/{orderId}/price", params);
        ResponseEntity<Double> response = restTemplate.getForEntity(url, Double.class);
        return response.getBody();
    }
}