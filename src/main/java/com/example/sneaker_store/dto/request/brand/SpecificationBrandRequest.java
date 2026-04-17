package com.example.sneaker_store.dto.request.brand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecificationBrandRequest {
    private String keyword;
    private String name;
    private String countryCode;
}
