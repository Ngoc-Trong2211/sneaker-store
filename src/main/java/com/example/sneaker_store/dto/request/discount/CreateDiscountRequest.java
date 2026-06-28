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
public class CreateDiscountRequest {
    @NotNull(message = "Phần trăm giảm giá là bắt buộc")
    private int percent;

    @NotBlank(message = "Mô tả là bắt buộc")
    private String description;

    @NotBlank(message = "Phạm vi áp dụng là bắt buộc")
    private String applyFor;

    private String nameApply;
    
    @NotNull(message = "Thời gian bắt đầu là bắt buộc")
    @FutureOrPresent(message = "Thời gian bắt đầu phải ở hiện tại hoặc tương lai")
    private Instant startTime;

    @NotNull(message = "Thời gian kết thúc là bắt buộc")
    @Future(message = "Thời gian kết thúc phải ở tương lai")
    private Instant endTime;
}
