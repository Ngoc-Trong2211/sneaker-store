package com.example.sneaker_store.dto.request.productVariant;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateProductVariantRequest {
    @NotBlank(message = "Color is required")
    private String color;

    @NotBlank(message = "Product ID is required")
    private String productName;

    @Size(max = 6, message = "Maximum 6 images allowed")
    private List<String> images;

    @NotEmpty(message = "Sizes is required")
    private List<SizeRequest> sizes;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SizeRequest {

        @NotBlank(message = "Size is required")
        private String size;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be > 0")
        private Integer quantity;
    }
}
