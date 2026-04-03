package com.example.sneaker_store.model.request.product;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductRequest {
    @NotNull(message = "Product ID is required")
    private String id;

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

    @Size(max = 6, message = "Maximum 6 images allowed")
    private List<String> images;
}
