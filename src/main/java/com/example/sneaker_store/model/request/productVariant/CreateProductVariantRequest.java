package com.example.sneaker_store.model.request.productVariant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductVariantRequest {
    @NotBlank(message = "Size is required")
    private String size;

    @NotBlank(message = "Color is required")
    private String color;

    @NotNull(message = "Stock is required")
    private Integer stock;

    @NotBlank(message = "Product ID is required")
    private String productId;
}
