package com.example.warehouse.exception;

public class ItemExistenceException extends RuntimeException {
    public ItemExistenceException(String message) {
        super(message);
    }
}
