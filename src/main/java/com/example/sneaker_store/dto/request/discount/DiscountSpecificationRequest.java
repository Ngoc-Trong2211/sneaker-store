package com.example.sneaker_store.dto.request.discount;

import com.example.sneaker_store.util.enumEntity.DiscountStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountSpecificationRequest {
    private String nameApply;
    private String applyFor;
    private DiscountStatus status;
}
