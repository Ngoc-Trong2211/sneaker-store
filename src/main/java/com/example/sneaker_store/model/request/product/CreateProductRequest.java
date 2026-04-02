package com.example.sneaker_store.model.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Product description is required")
    private String description;

    @NotNull(message = "Product price is required")
    private double price;
    
    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
