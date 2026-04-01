package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.ProductImageEntity;
import com.example.sneaker_store.model.request.productImage.CreateProductImageRequest;
import com.example.sneaker_store.model.response.productImage.CreateProductImageResponse;
import com.example.sneaker_store.repository.ProductImageRepository;
import com.example.sneaker_store.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "PRODUCT-IMAGE-SERVICE")
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ModelMapper modelMapper;

    @Override
    public CreateProductImageResponse createProductImage(CreateProductImageRequest req) {
        ProductImageEntity img = new ProductImageEntity();
        img.setImageURL(req.getImageURL());
        img.setMain(req.isMain());

        this.productImageRepository.save(img);
        return this.modelMapper.map(img, CreateProductImageResponse.class);
    }
}
