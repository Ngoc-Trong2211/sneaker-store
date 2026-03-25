package com.example.sneaker_store.util.exception.User;

public class EmailExistsAlreadyException extends RuntimeException {
    public EmailExistsAlreadyException(String message) {
        super(message);
    }
}
