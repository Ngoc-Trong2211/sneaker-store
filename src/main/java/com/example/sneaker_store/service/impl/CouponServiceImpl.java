package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.CouponEntity;
import com.example.sneaker_store.dto.request.coupon.CreateCouponRequest;
import com.example.sneaker_store.dto.request.coupon.UpdateCouponRequest;
import com.example.sneaker_store.dto.response.coupon.CreateCouponResponse;
import com.example.sneaker_store.dto.response.coupon.UpdateCouponResponse;
import com.example.sneaker_store.repository.CouponRepository;
import com.example.sneaker_store.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "COUPON-SERVICE")
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final ModelMapper modelMapper;

    @Override
    public CreateCouponResponse createCoupon(CreateCouponRequest req) {
        CouponEntity coupon = new CouponEntity();
        coupon.setCode(req.getCode());
        coupon.setQuantity(req.getQuantity());
        coupon.setType(req.getType());
        coupon.setDiscountValue(req.getDiscountValue());
        coupon.setExpiresAt(req.getExpiresAt());
        coupon.setMinOrderValue(req.getMinOrderValue());
        this.couponRepository.save(coupon);
        return this.modelMapper.map(coupon, CreateCouponResponse.class);
    }

    @Override
    public UpdateCouponResponse updateCoupon(UpdateCouponRequest req) {
        CouponEntity coupon = this.couponRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        coupon.setCode(req.getCode());
        coupon.setQuantity(req.getQuantity());
        coupon.setType(req.getType());
        coupon.setDiscountValue(req.getDiscountValue());
        coupon.setExpiresAt(req.getExpiresAt());
        coupon.setMinOrderValue(req.getMinOrderValue());

        this.couponRepository.save(coupon);

        return this.modelMapper.map(coupon, UpdateCouponResponse.class);
    }
}
