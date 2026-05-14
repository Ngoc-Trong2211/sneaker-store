package com.example.sneaker_store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSizeRequest {
    @NotBlank(message = "Variant id must not be empty!")
    private String variantId;

    @NotNull(message = "Id must not be empty!")
    private Long id;

    @NotBlank(message = "Size must not be empty!")
    private String size;

    @NotNull(message = "Quantity must not be empty!")
    private int quantity;
}
