package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.cartItem.CreateCartItemRequest;
import com.example.sneaker_store.model.response.cartItem.CreateCartItemResponse;

public interface CartItemService {
    CreateCartItemResponse createCartItem(CreateCartItemRequest req);
}
