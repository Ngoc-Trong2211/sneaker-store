package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.BrandEntity;
import com.example.sneaker_store.model.request.brand.CreateBrandRequest;
import com.example.sneaker_store.model.response.brand.CreateBrandResponse;
import com.example.sneaker_store.repository.BrandRepository;
import com.example.sneaker_store.service.BrandService;
import com.example.sneaker_store.util.exception.brand.NameExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "BRAND-SERVICE")
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final ModelMapper modelMapper;

    @Override
    public CreateBrandResponse create(CreateBrandRequest req) {
        BrandEntity brand = new BrandEntity();
        if (this.brandRepository.existsByName(req.getName().toUpperCase()))
            throw new NameExistsException("Name is exists");
        brand.setName(req.getName().toUpperCase());
        this.brandRepository.save(brand);
        return this.modelMapper.map(brand, CreateBrandResponse.class);
    }
}
