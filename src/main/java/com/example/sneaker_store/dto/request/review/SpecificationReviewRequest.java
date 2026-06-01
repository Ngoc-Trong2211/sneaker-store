package com.example.sneaker_store.dto.request.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecificationReviewRequest {
    private Integer star;
    private String dateFrom;
    private String dateTo;
    private String phone;
}
