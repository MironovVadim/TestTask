package com.example.orders.exception;

public class NoOrderException extends RuntimeException {

    public NoOrderException(String message) {
        super(message);
    }
}