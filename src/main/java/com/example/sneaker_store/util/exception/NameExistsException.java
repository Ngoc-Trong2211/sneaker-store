package com.example.sneaker_store.util.exception;

public class NameExistsException extends RuntimeException {
    public NameExistsException(String message) {
        super(message);
    }
}
