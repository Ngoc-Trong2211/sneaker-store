package com.example.sneaker_store.dto.response.productVariant;

import com.example.sneaker_store.dto.response.product.GetProductResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetVariantByIdResponse {
    private String id;
    private String color;
    private int stock;
    private String sku;
    private String status;
    private String brandName;
    private String productName;
    private List<ProductImage> images;
    private List<ProductSize> sizes;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductSize{
        private Long id;
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
