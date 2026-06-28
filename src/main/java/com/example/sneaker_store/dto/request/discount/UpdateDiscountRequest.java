package com.example.sneaker_store.dto.request.discount;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDiscountRequest {
    @NotNull(message = "ID chương trình giảm giá không được để trống")
    private String id;

    private String nameApply;

    @NotBlank(message = "Phạm vi áp dụng không được để trống")
    private String applyFor;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    private int percent;

    @NotNull(message = "Thời gian bắt đầu là bắt buộc")
    private Instant startTime;

    @NotNull(message = "Thời gian kết thúc là bắt buộc")
    @Future(message = "Thời gian kết thúc phải ở tương lai")
    private Instant endTime;
}
