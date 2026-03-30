package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.brand.CreateBrandRequest;
import com.example.sneaker_store.model.request.brand.UpdateBrandRequest;
import com.example.sneaker_store.model.response.brand.CreateBrandResponse;
import com.example.sneaker_store.model.response.brand.UpdateBrandResponse;

public interface BrandService {
    CreateBrandResponse createBrand(CreateBrandRequest req);
    UpdateBrandResponse updateBrand(UpdateBrandRequest req);
}
