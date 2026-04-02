package com.example.sneaker_store.model.request.discount;

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
    @NotNull(message = "Percent is required")
    private int percent;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Apply for is required")
    private String applyFor;

    @NotBlank(message = "Name apply is required")
    private String nameApply;
    
    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be in the present or future")
    private Instant startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private Instant endTime;
}
