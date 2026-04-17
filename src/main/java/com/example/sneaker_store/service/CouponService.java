package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.coupon.CreateCouponRequest;
import com.example.sneaker_store.dto.request.coupon.UpdateCouponRequest;
import com.example.sneaker_store.dto.response.coupon.CreateCouponResponse;
import com.example.sneaker_store.dto.response.coupon.UpdateCouponResponse;

public interface CouponService {
    CreateCouponResponse createCoupon(CreateCouponRequest req);
    UpdateCouponResponse updateCoupon(UpdateCouponRequest req);
}
