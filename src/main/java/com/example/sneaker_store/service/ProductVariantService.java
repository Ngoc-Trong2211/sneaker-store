package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.response.productVariant.*;
import org.springframework.data.domain.Pageable;

import com.example.sneaker_store.dto.request.productVariant.CreateProductVariantRequest;
import com.example.sneaker_store.dto.request.productVariant.SpecificationProductVariantRequest;
import com.example.sneaker_store.dto.request.productVariant.UpdateProductVariantRequest;

public interface ProductVariantService {
    CreateProductVariantResponse createProductVariant(CreateProductVariantRequest request);
    UpdateProductVariantResponse updateProductVariant(UpdateProductVariantRequest request);
    GetProductVariantResponse getProductVariant(Pageable pageable, SpecificationProductVariantRequest request);
    void deleteProductVariant(String id);
    void updateProductVariantStatus(String id, String status);
    GetVariantByIdResponse getVariantById(String id);
    GetVariantBySkuResponse getVariantBySku(String sku);
}
