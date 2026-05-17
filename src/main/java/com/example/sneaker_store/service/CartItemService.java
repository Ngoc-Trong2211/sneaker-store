package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.cartItem.CreateCartItemRequest;
import com.example.sneaker_store.dto.request.cartItem.UpdateQuantityRequest;
import com.example.sneaker_store.dto.response.cartItem.CreateCartItemResponse;
import com.example.sneaker_store.dto.response.cartItem.GetCartResponse;

public interface CartItemService {
    CreateCartItemResponse addToCart(CreateCartItemRequest req, String guestId);
    void deleteCartItem(Long id);
    int updateQuantity(UpdateQuantityRequest req);
}
