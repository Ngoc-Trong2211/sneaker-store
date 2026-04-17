package com.example.sneaker_store.dto.response.productImage;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GetProductImageResponse {
    private List<ProductImage> images;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImage {
        private String imageURL;
        private boolean isMain;  
    }
}
