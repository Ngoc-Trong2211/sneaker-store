package com.example.sneaker_store.dto.response.productVariant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetVariantBySkuResponse {
    private String id;
    private String size;
    private String color;
    private int stock;
    private String sku;
    private List<ProductImage> images;
    private Product product;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductImage{
        private String url;
        private boolean isMain;
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
        private String title;
    }
}
