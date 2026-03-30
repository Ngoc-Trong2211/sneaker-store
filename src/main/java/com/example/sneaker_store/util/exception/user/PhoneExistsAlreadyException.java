package com.example.sneaker_store.util.exception.user;

public class PhoneExistsAlreadyException extends RuntimeException {
    public PhoneExistsAlreadyException(String message) {
        super(message);
    }
}
