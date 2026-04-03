package com.example.sneaker_store.service;

import org.springframework.data.domain.Pageable;

import com.example.sneaker_store.model.request.productVariant.CreateProductVariantRequest;
import com.example.sneaker_store.model.request.productVariant.SpecificationProductVariantRequest;
import com.example.sneaker_store.model.request.productVariant.UpdateProductVariantRequest;
import com.example.sneaker_store.model.response.productVariant.CreateProductVariantResponse;
import com.example.sneaker_store.model.response.productVariant.GetProductVariantResponse;
import com.example.sneaker_store.model.response.productVariant.UpdateProductVariantResponse;

public interface ProductVariantService {
    CreateProductVariantResponse createProductVariant(CreateProductVariantRequest request);
    UpdateProductVariantResponse updateProductVariant(UpdateProductVariantRequest request);
    GetProductVariantResponse getProductVariant(Pageable pageable, SpecificationProductVariantRequest request);
    void deleteProductVariant(String id);
}
