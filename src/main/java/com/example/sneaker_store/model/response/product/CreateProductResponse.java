package com.example.sneaker_store.model.response.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductResponse {
    private String id;

    private String name;
    private String description;
    private double price;
    private String slug;
    private String brandName;
    private String status;
    private String categoryName;
}
