package com.example.warehouse.exception;

public class NoOrderException extends RuntimeException {
    public NoOrderException(String message) {
        super(message);
    }
}
