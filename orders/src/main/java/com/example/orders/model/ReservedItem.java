package com.example.orders.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ReservedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JoinColumn(name = "id", table = "orders")
    private Integer orderId;
}
