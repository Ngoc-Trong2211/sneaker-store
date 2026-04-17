package com.example.sneaker_store.service;

import org.springframework.data.domain.Pageable;

import com.example.sneaker_store.dto.request.discount.CreateDiscountRequest;
import com.example.sneaker_store.dto.request.discount.DiscountSpecificationRequest;
import com.example.sneaker_store.dto.request.discount.UpdateDiscountRequest;
import com.example.sneaker_store.dto.response.discount.CreateDiscountResponse;
import com.example.sneaker_store.dto.response.discount.GetDiscountResponse;
import com.example.sneaker_store.dto.response.discount.UpdateDiscountResponse;
import com.example.sneaker_store.util.enumEntity.DiscountStatus;

public interface DiscountService {
    CreateDiscountResponse createDiscount(CreateDiscountRequest request);
    UpdateDiscountResponse updateDiscount(UpdateDiscountRequest request);
    GetDiscountResponse.Discount getDiscountById(String id);
    void updateStatusDiscount(String id, DiscountStatus status);
    GetDiscountResponse getDiscounts(DiscountSpecificationRequest request, Pageable pageable);
    void deleteDiscount(String id);
}
