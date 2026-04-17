package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.cartItem.CreateCartItemRequest;
import com.example.sneaker_store.dto.response.cartItem.CreateCartItemResponse;

public interface CartItemService {
    CreateCartItemResponse addToCart(CreateCartItemRequest req);
    void deleteCartItem(Long id);
}
