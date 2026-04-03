package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.productVariant.CreateProductVariantRequest;
import com.example.sneaker_store.model.request.productVariant.UpdateProductVariantRequest;
import com.example.sneaker_store.model.response.productVariant.CreateProductVariantResponse;
import com.example.sneaker_store.model.response.productVariant.UpdateProductVariantResponse;

public interface ProductVariantService {
    CreateProductVariantResponse createProductVariant(CreateProductVariantRequest request);
    UpdateProductVariantResponse updateProductVariant(UpdateProductVariantRequest request);
}
