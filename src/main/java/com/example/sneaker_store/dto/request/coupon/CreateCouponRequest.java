package com.example.sneaker_store.dto.request.coupon;

import com.example.sneaker_store.util.enumEntity.CouponType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CreateCouponRequest {
    private String code;
    private int discountValue;
    private int minOrderValue;
    private Instant expiresAt;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private CouponType type;
}
