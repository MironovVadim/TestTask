package com.example.warehouse.exception;

public class OrderStateException extends RuntimeException {
    public OrderStateException(String message) {
        super(message);
    }
}
