package com.example.sneaker_store.dto.request.cartItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateQuantityRequest {
    @NotNull(message = "ID là bắt buộc")
    private Long id;

    @NotBlank(message = "Thao tác là bắt buộc")
    private String action;

    @NotNull(message = "ID kích cỡ sản phẩm là bắt buộc")
    private Long idSize;
}
