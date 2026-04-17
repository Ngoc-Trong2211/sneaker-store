package com.example.sneaker_store.dto.request.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    private String phone;
    private String receiverName;
    private String address;
}
