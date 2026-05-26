package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.order.CreateOrderRequest;
import com.example.sneaker_store.dto.request.order.SpecificationOrderRequest;
import com.example.sneaker_store.dto.response.order.CreateOrderResponse;
import com.example.sneaker_store.dto.response.order.GetOrderResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request, String guestId);
    GetOrderResponse getOrder(Pageable pageable, SpecificationOrderRequest spec);
    void updateStatus(String id, String status, String lyDoHuy);
    GetOrderResponse getOrderByUser(Pageable pageable, String dateFrom, String dateTo, String status);
    void cancelOrder (String code, String lyDoHuy);
}
