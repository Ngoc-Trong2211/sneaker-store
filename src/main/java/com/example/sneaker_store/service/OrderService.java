package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.order.CreateOrderRequest;
import com.example.sneaker_store.dto.response.order.CreateOrderResponse;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request, String guestId);
}
