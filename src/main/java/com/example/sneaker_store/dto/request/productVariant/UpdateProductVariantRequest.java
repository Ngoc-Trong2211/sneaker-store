package com.example.sneaker_store.dto.request.productVariant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateProductVariantRequest {
    @NotBlank(message = "Product variant ID cannot be null")
    private String id;

    @NotBlank(message = "Size cannot be null")
    private String size;

    @NotBlank(message = "Color cannot be null")
    private String color;

    @NotNull(message = "Stock cannot be null")
    private Integer stock;

    @NotBlank(message = "Product Name is required")
    private String productName;

    @Size(max = 6, message = "Maximum 6 images allowed")
    private List<String> images;
}
