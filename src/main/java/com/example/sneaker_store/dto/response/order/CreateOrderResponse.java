package com.example.sneaker_store.dto.response.order;

import com.example.sneaker_store.util.enumEntity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderResponse {
    private String id;

    private double totalAmount;
    private OrderStatus status;

    private String guestPhone;
    private String guestName;
    private String address;
}
