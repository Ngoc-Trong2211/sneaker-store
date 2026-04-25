package com.example.sneaker_store.dto.response.productVariant;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class    GetProductVariantResponse {
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
        private String brandName;
        private String productName;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
        private Instant createdAt;
        private String createdBy;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
        private Instant updatedAt;
        private String updatedBy;
    }
}
