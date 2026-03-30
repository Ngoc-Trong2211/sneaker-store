package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.brand.CreateBrandRequest;
import com.example.sneaker_store.model.response.brand.CreateBrandResponse;

public interface BrandService {
    CreateBrandResponse create(CreateBrandRequest req);
}
