package com.example.sneaker_store.model.request.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    private String guestAddress;
    private String guestPhone;
    private String guestName;
    private String address;
}
