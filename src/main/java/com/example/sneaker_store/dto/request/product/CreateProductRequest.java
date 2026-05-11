package com.example.sneaker_store.dto.request.product;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private Double price;
    
    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Title description is required")
    private String title;
}
