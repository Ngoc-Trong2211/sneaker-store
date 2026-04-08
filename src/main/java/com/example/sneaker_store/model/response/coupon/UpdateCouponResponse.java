package com.example.sneaker_store.model.response.coupon;

import com.example.sneaker_store.util.enumEntity.CouponType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class UpdateCouponResponse {
    private Long id;

    private String code;
    private int discountValue;
    private int minOrderValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant expiresAt;
    private int quantity;

    @Enumerated(EnumType.STRING)
    private CouponType type;
}
