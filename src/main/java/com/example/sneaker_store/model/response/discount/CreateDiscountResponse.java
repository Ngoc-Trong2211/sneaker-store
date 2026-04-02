package com.example.sneaker_store.model.response.discount;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDiscountResponse {
    private String id;
    private int percent;
    private String description;
    private String applyFor;
    private String nameApply;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant endTime;
}
