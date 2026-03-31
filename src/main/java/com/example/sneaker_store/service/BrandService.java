package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.brand.CreateBrandRequest;
import com.example.sneaker_store.model.request.brand.UpdateBrandRequest;
import com.example.sneaker_store.model.response.brand.CreateBrandResponse;
import com.example.sneaker_store.model.response.brand.GetBrandResponse;
import com.example.sneaker_store.model.response.brand.UpdateBrandResponse;
import org.springframework.data.domain.Pageable;

import java.net.URISyntaxException;

public interface BrandService {
    CreateBrandResponse createBrand(CreateBrandRequest req);
    UpdateBrandResponse updateBrand(UpdateBrandRequest req) throws URISyntaxException;
    GetBrandResponse getBrand(Pageable pageable, String name);
    void deleteBrand(Long id) throws URISyntaxException;
}
