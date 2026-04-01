package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.productImage.CreateProductImageRequest;
import com.example.sneaker_store.model.request.productImage.UpdateProductImageRequest;
import com.example.sneaker_store.model.response.productImage.CreateProductImageResponse;
import com.example.sneaker_store.model.response.productImage.UpdateProductImageResponse;

public interface ProductImageService {
    CreateProductImageResponse createProductImage(CreateProductImageRequest req);
    UpdateProductImageResponse updateProductImage(UpdateProductImageRequest req);
    void deleteProductImage(Long id);
}
