package com.example.orders.model.dto;

import com.example.orders.model.Orders;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {
    private String userName;
    private List<ReservedItemDto> reservedItems;

    public Orders createOrderFromDto() {
        Orders order = new Orders();
        order.setUserName(this.getUserName());
        return order;
    }
}