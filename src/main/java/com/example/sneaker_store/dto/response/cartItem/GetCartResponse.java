package com.example.sneaker_store.dto.response.cartItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetCartResponse {
    private String guestId;
    private String userId;
    private List<CartItem> cartItems;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItem{
        private Long id;
        private String nameProduct;
        private String size;
        private String color;
        private int quantity;
        private Double price;
        private String slugCategory;
        private String brandName;
        private String sku;
        private String url;
        private Long idSize;
    }
}
