package com.example.sneaker_store.dto.response.productVariant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductVariantResponse {
    private String id;
    private String size;
    private String color;
    private int stock;
    private String sku;
}
