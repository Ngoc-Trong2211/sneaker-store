package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.order.CreateOrderRequest;
import com.example.sneaker_store.dto.request.order.SpecificationOrderRequest;
import com.example.sneaker_store.dto.response.order.CreateOrderResponse;
import com.example.sneaker_store.dto.response.order.GetOrderResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request, String guestId);
    GetOrderResponse getOrder(Pageable pageable, SpecificationOrderRequest spec);
}
