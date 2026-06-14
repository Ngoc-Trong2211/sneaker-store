package com.example.sneaker_store.dto.response.order;

import com.example.sneaker_store.util.enumEntity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentStatusResponse {
    private String code;
    private String paymentCode;
    private OrderStatus status;
    private double totalAmount;
    private boolean paid;
}
