package com.example.sneaker_store.service;

import com.example.sneaker_store.model.response.cart.CreateCartResponse;

public interface CartService {
    CreateCartResponse createCart(String guestId);
}
