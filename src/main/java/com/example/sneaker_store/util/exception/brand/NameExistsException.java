package com.example.sneaker_store.util.exception.brand;

public class NameExistsException extends RuntimeException {
    public NameExistsException(String message) {
        super(message);
    }
}
