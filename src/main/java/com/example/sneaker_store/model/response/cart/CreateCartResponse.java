package com.example.sneaker_store.model.response.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCartResponse {
    private Long id;
    private String guestId;
    private String userId;
}
