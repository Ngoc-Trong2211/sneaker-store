package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.discount.CreateDiscountRequest;
import com.example.sneaker_store.model.response.discount.CreateDiscountResponse;

public interface DiscountService {
    CreateDiscountResponse createDiscount(CreateDiscountRequest request);
}
