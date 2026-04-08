package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.coupon.CreateCouponRequest;
import com.example.sneaker_store.model.response.coupon.CreateCouponResponse;

public interface CouponService {
    CreateCouponResponse createCoupon(CreateCouponRequest req);
}
