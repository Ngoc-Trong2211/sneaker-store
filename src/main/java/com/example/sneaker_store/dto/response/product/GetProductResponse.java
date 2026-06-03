package com.example.sneaker_store.dto.response.product;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        private String price;
        private String status;
        private String quantity;
        private String slug;
        private String slugCategory;
        private String brandName;
        private List<Variant> variants;
        private Integer percent;
        private Boolean favourite;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Variant{
            private String id;
            private String color;
            private int stock;
            private String sku;
            private List<ProductImage> images;
            private List<ProductSize> sizes;

            @Getter
            @Setter
            @AllArgsConstructor
            @NoArgsConstructor
            public static class ProductSize{
                private String size;
                private int quantity;
            }

            @Getter
            @Setter
            @AllArgsConstructor
            @NoArgsConstructor
            public static class ProductImage{
                private String url;
                private boolean isMain;
            }
        }

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
        private Instant createdAt;
        private String createdBy;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
        private Instant updatedAt;
        private String updatedBy;
    }
}
