package com.example.sneaker_store.model.request.productVariant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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

    @NotBlank(message = "SKU cannot be null")
    private String sku;
}
