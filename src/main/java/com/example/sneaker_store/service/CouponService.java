package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.coupon.CouponSpecificationRequest;
import com.example.sneaker_store.dto.request.coupon.CreateCouponRequest;
import com.example.sneaker_store.dto.request.coupon.UpdateCouponRequest;
import com.example.sneaker_store.dto.response.coupon.CreateCouponResponse;
import com.example.sneaker_store.dto.response.coupon.GetCouponResponse;
import com.example.sneaker_store.dto.response.coupon.UpdateCouponResponse;
import com.example.sneaker_store.dto.response.coupon.ValidateCouponResponse;
import org.springframework.data.domain.Pageable;

public interface CouponService {
    CreateCouponResponse createCoupon(CreateCouponRequest req);
    UpdateCouponResponse updateCoupon(UpdateCouponRequest req);
    GetCouponResponse.Coupon getCouponById(Long id);
    GetCouponResponse getCoupons(CouponSpecificationRequest request, Pageable pageable);
    ValidateCouponResponse validateCoupon(String code, double orderAmount);
    double useCoupon(String code, double orderAmount);
    void deleteCoupon(Long id);
}
