package com.example.sneaker_store.dto.response.coupon;

import com.example.sneaker_store.util.enumEntity.CouponType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateCouponResponse {
    private boolean valid;
    private String message;
    private String code;
    private CouponType type;
    private int discountValue;
    private int minOrderValue;
    private double orderAmount;
    private double discountAmount;
    private double totalAfterDiscount;
}
