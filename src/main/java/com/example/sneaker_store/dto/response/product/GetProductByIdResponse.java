package com.example.sneaker_store.dto.response.product;

import java.time.Instant;
import java.util.List;

import com.example.sneaker_store.dto.response.productVariant.GetVariantByIdResponse;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GetProductByIdResponse {
    private String id;
    private String name;
    private String description;
    private String price;
    private String status;
    private String slug;
    private int quantity;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private String title;
    private List<Variant> variants;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Variant{
        private String id;
        private String size;
        private String color;
        private int stock;
        private String sku;
        private List<ProductImage> images;

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
}
