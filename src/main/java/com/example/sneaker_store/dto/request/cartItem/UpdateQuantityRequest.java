package com.example.sneaker_store.dto.request.cartItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateQuantityRequest {
    @NotNull(message = "Id is required")
    private Long id;

    @NotBlank(message = "action is required")
    private String action;

    @NotNull(message = "Product size id is required")
    private Long idSize;
}
