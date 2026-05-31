package com.example.sneaker_store.dto.response.order;

import com.example.sneaker_store.util.enumEntity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class CreateOrderResponse {
    private String id;
    private String code;
    private double totalAmount;
    private OrderStatus status;
    private String email;
    private String phone;
    private String receiverName;
    private String guestPhone;
    private String guestName;
    private String address;
    private Instant createdAt;
    private List<OrderItem> orderItems;

    @Getter
    @Setter
    public static class OrderItem {
        private String id;
        private String productId;
        private String productName;
        private Integer quantity;
        private String size;
        private Double price;
        private Integer percent;
    }
}
