package com.example.sneaker_store.dto.response.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class GetOrderResponse {
    private DataPage dataPage;
    private List<Order> orders;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataPage{
        private int number;
        private int size;
        private int numberOfElements;
        private int totalPages;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Order{
        private String id;
        private String address;
        private String phone;
        private String receiverName;
        private String status;
        private Double subTotalAmount;
        private Double couponDiscountAmount;
        private String couponCode;
        private Double totalAmount;
        private String code;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
        private Instant createdAt;
        private List<OrderItem> orderItems;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OrderItem{
            private String id;
            private String productId;
            private String productName;
            private Integer quantity;
            private String size;
            private Double price;
            private String url;
            private Boolean reviewStatus;
            private Boolean canReview;
            private Integer percent;
        }
    }
}
