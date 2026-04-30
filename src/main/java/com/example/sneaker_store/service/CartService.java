package com.example.sneaker_store.service;

import com.example.sneaker_store.model.CartEntity;

public interface CartService {
    CartEntity createCart(String guestId);
    void mergeCart(String userId, String guestId);
}
