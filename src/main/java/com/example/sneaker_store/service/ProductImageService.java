package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.productImage.CreateProductImageRequest;
import com.example.sneaker_store.model.response.productImage.CreateProductImageResponse;

public interface ProductImageService {
    CreateProductImageResponse createProductImage(CreateProductImageRequest req);
}
