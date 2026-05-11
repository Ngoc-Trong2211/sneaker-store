package com.example.sneaker_store.dto.response.productVariant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetVariantByIdResponse {
    private String id;
    private String size;
    private String color;
    private int stock;
    private String sku;
    private String status;
    private String brandName;
    private String productName;
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
