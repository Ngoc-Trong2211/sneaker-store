package com.example.sneaker_store.dto.request.cartItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCartItemRequest {
    @NotBlank(message = "ID biến thể sản phẩm là bắt buộc")
    private String variantId;

    private Long idSize;
}
