package com.example.sneaker_store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSizeRequest {
    @NotBlank(message = "ID biến thể không được để trống")
    private String variantId;

    @NotNull(message = "ID không được để trống")
    private Long id;

    @NotBlank(message = "Kích cỡ không được để trống")
    private String size;

    @NotNull(message = "Số lượng không được để trống")
    private int quantity;
}
