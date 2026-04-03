package com.example.sneaker_store.model.response.productVariant;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GetProductVariantResponse {
    private DataPage page;
    private List<ProductVariant> productVariants;

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
    public static class ProductVariant {
        private String id;
        private String size;
        private String color;
        private int stock;
        private String sku;
        private String status;
    }
}
