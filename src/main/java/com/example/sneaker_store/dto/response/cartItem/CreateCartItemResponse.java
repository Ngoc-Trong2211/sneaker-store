package com.example.sneaker_store.dto.response.cartItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCartItemResponse {
    private Long id;
    private String nameProduct;
    private String size;
    private String color;
    private int quantity;
}
