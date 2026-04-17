package com.example.sneaker_store.dto.response.discount;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDiscountResponse {
    private String id;
    private String nameApply;
    private String applyFor;
    private String description;
    private int percent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant endTime;
}
