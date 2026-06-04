package com.example.sneaker_store.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAiDto {
    private String productId;
    private String productName;
    private Double price;
    private String slug;
    private String description;
    private String categoryName;
    private String parentCategoryName;
    private String color;
    private String imageUrl;
}