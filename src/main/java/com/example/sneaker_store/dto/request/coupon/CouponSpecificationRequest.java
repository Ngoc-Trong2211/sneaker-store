package com.example.sneaker_store.dto.request.coupon;

import com.example.sneaker_store.util.enumEntity.CouponType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponSpecificationRequest {
    private String code;
    private CouponType type;
    private Boolean active;
}
