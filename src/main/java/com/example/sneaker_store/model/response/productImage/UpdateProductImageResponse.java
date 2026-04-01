package com.example.sneaker_store.model.response.productImage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductImageResponse {
    private String imageURL;
    private boolean isMain;
}
