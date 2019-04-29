package com.example.warehouse.model.dto;

import lombok.Data;

/**
 * Dto для проверки существующего заказа и его статуса, если статус не NEW,
 * значит данные некорректны и заказ не осуществится
 */
@Data
public class OrderDto {
    private Integer id;
    private String state;
}
