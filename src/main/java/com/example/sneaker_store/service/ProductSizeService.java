package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.UpdateSizeRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface ProductSizeService {
    void updateSize(String id,
                    Long id1,
                    @NotBlank(message = "Size is required") String size,
                    @NotNull(message = "Quantity is required")
                    @Min(value = 1, message = "Quantity must be > 0") Integer quantity);
}
