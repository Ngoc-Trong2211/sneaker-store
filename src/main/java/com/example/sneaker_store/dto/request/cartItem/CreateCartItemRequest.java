package com.example.sneaker_store.dto.request.cartItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCartItemRequest {
    @NotBlank(message = "Product variant id is required")
    private String variantId;

    private Long idSize;
}
