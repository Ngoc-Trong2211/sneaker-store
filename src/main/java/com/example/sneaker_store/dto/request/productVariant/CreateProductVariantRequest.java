package com.example.sneaker_store.dto.request.productVariant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private String productName;

    @Size(max = 6, message = "Maximum 6 images allowed")
    private List<String> images;
}
