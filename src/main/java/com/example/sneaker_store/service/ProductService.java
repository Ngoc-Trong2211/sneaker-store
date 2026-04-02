package com.example.sneaker_store.service;

import org.springframework.data.domain.Pageable;

import com.example.sneaker_store.model.request.product.CreateProductRequest;
import com.example.sneaker_store.model.request.product.SpecificationProductRequest;
import com.example.sneaker_store.model.request.product.UpdateProductRequest;
import com.example.sneaker_store.model.response.product.CreateProductResponse;
import com.example.sneaker_store.model.response.product.GetProductByIdResponse;
import com.example.sneaker_store.model.response.product.GetProductResponse;
import com.example.sneaker_store.model.response.product.UpdateProductResponse;

public interface ProductService {
    CreateProductResponse createProduct(CreateProductRequest request);
    UpdateProductResponse updateProduct(UpdateProductRequest request);
    GetProductResponse getProducts(Pageable pageable, SpecificationProductRequest request);
    GetProductByIdResponse getProductById(String id);
}
