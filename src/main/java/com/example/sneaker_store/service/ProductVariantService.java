package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.productVariant.CreateProductVariantRequest;
import com.example.sneaker_store.model.response.productVariant.CreateProductVariantResponse;

public interface ProductVariantService {
    CreateProductVariantResponse createProductVariant(CreateProductVariantRequest request);
}
