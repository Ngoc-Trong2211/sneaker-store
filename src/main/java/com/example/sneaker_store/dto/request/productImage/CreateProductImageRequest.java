package com.example.sneaker_store.dto.request.productImage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductImageRequest {
    private String imageURL;
    private boolean isMain;
}