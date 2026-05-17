package com.example.sneaker_store.dto.request.cartItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateQuantityRequest {
    private Long id;
    private Integer quantity;
    private String action;
}
