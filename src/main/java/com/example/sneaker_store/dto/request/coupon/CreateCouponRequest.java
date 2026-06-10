package com.example.sneaker_store.dto.request.coupon;

import com.example.sneaker_store.util.enumEntity.CouponType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CreateCouponRequest {
    @NotBlank(message = "Coupon code cannot be blank")
    private String code;

    @Min(value = 1, message = "Discount value must be greater than 0")
    private int discountValue;

    @Min(value = 0, message = "Minimum order value cannot be negative")
    private int minOrderValue;

    @NotNull(message = "Expires at cannot be null")
    @Future(message = "Expires at must be in the future")
    private Instant expiresAt;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;

    @NotNull(message = "Coupon type cannot be null")
    private CouponType type;
}
