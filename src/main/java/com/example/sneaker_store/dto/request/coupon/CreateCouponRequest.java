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
    @NotBlank(message = "Mã giảm giá không được để trống")
    private String code;

    @Min(value = 1, message = "Giá trị giảm phải lớn hơn 0")
    private int discountValue;

    @Min(value = 0, message = "Giá trị đơn hàng tối thiểu không được âm")
    private int minOrderValue;

    @NotNull(message = "Thời gian hết hạn không được để trống")
    @Future(message = "Thời gian hết hạn phải ở tương lai")
    private Instant expiresAt;

    @Min(value = 0, message = "Số lượng không được âm")
    private int quantity;

    @NotNull(message = "Loại mã giảm giá không được để trống")
    private CouponType type;
}
