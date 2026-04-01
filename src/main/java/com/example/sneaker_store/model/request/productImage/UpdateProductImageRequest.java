package com.example.sneaker_store.model.request.productImage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductImageRequest {
    private Long id;
    private String imageURL;
    private boolean isMain;
}