package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.product.CreateProductRequest;
import com.example.sneaker_store.model.response.product.CreateProductResponse;

public interface ProductService {
    CreateProductResponse createProduct(CreateProductRequest request);
}
