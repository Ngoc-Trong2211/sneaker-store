package com.example.sneaker_store.dto.response.coupon;

import com.example.sneaker_store.util.enumEntity.CouponType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CreateCouponResponse {
    private Long id;

    private String code;
    private int discountValue;
    private int minOrderValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant expiresAt;
    private int quantity;

    private CouponType type;
}
