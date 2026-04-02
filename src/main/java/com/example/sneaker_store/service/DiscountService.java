package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.discount.CreateDiscountRequest;
import com.example.sneaker_store.model.request.discount.UpdateDiscountRequest;
import com.example.sneaker_store.model.response.discount.CreateDiscountResponse;
import com.example.sneaker_store.model.response.discount.UpdateDiscountResponse;

public interface DiscountService {
    CreateDiscountResponse createDiscount(CreateDiscountRequest request);
    UpdateDiscountResponse updateDiscount(UpdateDiscountRequest request);
}
