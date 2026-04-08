package com.example.sneaker_store.service;

import com.example.sneaker_store.model.OrderEntity;

public interface OrderItemService {
    double addToOrder(String guestId, OrderEntity order);
}
