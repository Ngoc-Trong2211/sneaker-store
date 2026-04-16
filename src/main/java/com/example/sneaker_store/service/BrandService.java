package com.example.sneaker_store.service;

import com.example.sneaker_store.model.BrandEntity;
import com.example.sneaker_store.model.request.brand.CreateBrandRequest;
import com.example.sneaker_store.model.request.brand.SpecificationBrandRequest;
import com.example.sneaker_store.model.request.brand.UpdateBrandRequest;
import com.example.sneaker_store.model.response.brand.CreateBrandResponse;
import com.example.sneaker_store.model.response.brand.GetBrandResponse;
import com.example.sneaker_store.model.response.brand.UpdateBrandResponse;
import org.springframework.data.domain.Pageable;

import java.net.URISyntaxException;

public interface BrandService {
    CreateBrandResponse createBrand(CreateBrandRequest req);
    UpdateBrandResponse updateBrand(UpdateBrandRequest req) throws URISyntaxException;
    GetBrandResponse getBrand(Pageable pageable, SpecificationBrandRequest request);
    void deleteBrand(Long id) throws URISyntaxException;
    BrandEntity findById(Long id);
    BrandEntity findByName(String name);
}
