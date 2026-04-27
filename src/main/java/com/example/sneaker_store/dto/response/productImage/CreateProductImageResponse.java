package com.example.sneaker_store.dto.response.productImage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductImageResponse {
    private String imageURL;
    private boolean isMain;
    private String publicId;
}
