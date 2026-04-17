package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.productImage.CreateProductImageRequest;
import com.example.sneaker_store.dto.request.productImage.UpdateProductImageRequest;
import com.example.sneaker_store.dto.response.productImage.CreateProductImageResponse;
import com.example.sneaker_store.dto.response.productImage.GetProductImageResponse;
import com.example.sneaker_store.dto.response.productImage.UpdateProductImageResponse;

public interface ProductImageService {
    CreateProductImageResponse createProductImage(CreateProductImageRequest req);
    UpdateProductImageResponse updateProductImage(UpdateProductImageRequest req);
    void deleteProductImage(Long id);
    GetProductImageResponse getProductImageById(String productId);
}
