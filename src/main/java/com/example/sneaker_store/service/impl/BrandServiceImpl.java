package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.BrandEntity;
import com.example.sneaker_store.model.request.brand.CreateBrandRequest;
import com.example.sneaker_store.model.request.brand.UpdateBrandRequest;
import com.example.sneaker_store.model.response.brand.CreateBrandResponse;
import com.example.sneaker_store.model.response.brand.UpdateBrandResponse;
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
    public CreateBrandResponse createBrand(CreateBrandRequest req) {
        BrandEntity brand = new BrandEntity();
        if (this.brandRepository.existsByName(req.getName().toUpperCase()))
            throw new NameExistsException("Name is exists");
        brand.setName(req.getName().toUpperCase());
        this.brandRepository.save(brand);
        return this.modelMapper.map(brand, CreateBrandResponse.class);
    }

    @Override
    public UpdateBrandResponse updateBrand(UpdateBrandRequest req) {
        BrandEntity brand = brandRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy brand!"));
        if (brandRepository.existsByNameAndIdNot(req.getName().toUpperCase(), req.getId())) {
            throw new RuntimeException("Tên brand đã tồn tại!");
        }
        if (!brand.getName().equals(req.getName().toUpperCase())){
            brand.setName(req.getName().toUpperCase());
            brandRepository.save(brand);
        }

        return modelMapper.map(brand, UpdateBrandResponse.class);
    }
}
