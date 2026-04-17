package com.example.sneaker_store.dto.response.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GetProductResponse {
    private DataPage page;
    private List<Product> products;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataPage {
        private int number;
        private int size;
        private int numberOfElements;
        private int totalPages;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Product {
        private String id;
        private String name;
        private String description;
        private double price;
        private String status;
        private String categoryName;
        private String brandName;
    }
}
