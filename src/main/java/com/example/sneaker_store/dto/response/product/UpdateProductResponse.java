package com.example.sneaker_store.dto.response.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductResponse {
    private String id;
    private String name;
    private String description;
    private String price;
    private String slug;
    private String status;
    private String categoryName;
    private String brandName;
}
