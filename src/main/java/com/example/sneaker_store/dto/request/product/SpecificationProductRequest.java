package com.example.sneaker_store.dto.request.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecificationProductRequest {
    private String name;
    private Double minPrice;
    private Double maxPrice;
    private String status;
    private Long brandId;
    private Long categoryId;
}
