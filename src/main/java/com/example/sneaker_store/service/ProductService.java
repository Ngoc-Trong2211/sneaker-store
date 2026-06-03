package com.example.sneaker_store.service;

import org.springframework.data.domain.Pageable;

import com.example.sneaker_store.dto.request.product.CreateProductRequest;
import com.example.sneaker_store.dto.request.product.SpecificationProductRequest;
import com.example.sneaker_store.dto.request.product.UpdateProductRequest;
import com.example.sneaker_store.dto.response.product.CreateProductResponse;
import com.example.sneaker_store.dto.response.product.GetProductByIdResponse;
import com.example.sneaker_store.dto.response.product.GetProductResponse;
import com.example.sneaker_store.dto.response.product.UpdateProductResponse;
import com.example.sneaker_store.util.enumEntity.ProductStatus;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    CreateProductResponse createProduct(CreateProductRequest request);
    UpdateProductResponse updateProduct(UpdateProductRequest request);
    GetProductResponse getProducts(Pageable pageable, SpecificationProductRequest request, String guestId);
    GetProductByIdResponse getProductById(String id, String guestId);
    void updateStatusProduct(String id, String status);
    void deleteProduct(String id);
}
