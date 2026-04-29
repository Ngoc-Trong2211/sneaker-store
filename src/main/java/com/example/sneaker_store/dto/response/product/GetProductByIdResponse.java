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
public class GetProductByIdResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    private String status;
    private String slug;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private List<ProductImage> images;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private String createdBy;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductImage{
        private String url;
        private boolean isMain;
    }
}
