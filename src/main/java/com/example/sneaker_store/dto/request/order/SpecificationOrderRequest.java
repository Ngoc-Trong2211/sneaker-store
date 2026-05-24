package com.example.sneaker_store.dto.request.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecificationOrderRequest {
    private String status;
    private String dateFrom;
    private String dateTo;
    private String keySearch;
}
