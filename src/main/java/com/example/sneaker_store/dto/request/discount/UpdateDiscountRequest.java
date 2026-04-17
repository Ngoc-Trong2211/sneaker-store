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
    @NotNull(message = "Discount ID cannot be null")
    private String id;

    @NotBlank(message = "Name of the discount application cannot be blank")
    private String nameApply;

    @NotBlank(message = "Apply for cannot be blank")
    private String applyFor;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Percent cannot be null")
    private int percent;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be in the present or future")
    private Instant startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private Instant endTime;
}
