package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.UpdateSizeRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface ProductSizeService {
    void updateSize(String id,
                    Long id1,
            @NotBlank(message = "Kích cỡ là bắt buộc") String size,
            @NotNull(message = "Số lượng là bắt buộc")
            @Min(value = 1, message = "Số lượng phải lớn hơn 0") Integer quantity);
}
