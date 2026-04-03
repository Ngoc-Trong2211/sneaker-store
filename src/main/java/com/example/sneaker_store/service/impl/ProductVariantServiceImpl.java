package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.service.ProductVariantService;

import com.example.sneaker_store.model.ProductVariantEntity;
import com.example.sneaker_store.model.request.productVariant.CreateProductVariantRequest;
import com.example.sneaker_store.model.response.productVariant.CreateProductVariantResponse;
import com.example.sneaker_store.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "PRODUCT-VARIANT-SERVICE")
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ModelMapper modelMapper;

    @Override
    public CreateProductVariantResponse createProductVariant(CreateProductVariantRequest request) {
        log.info("Creating product variant with size: {}, color: {}, stock: {}, sku: {}",
                request.getSize(), request.getColor(), request.getStock(), request.getSku());
        if (this.productVariantRepository.findByColorAndSize(request.getColor(), request.getSize()) != null) {
            log.warn("Product variant with size: {} and color: {} already exists", request.getSize(), request.getColor());
            ProductVariantEntity existingVariant = this.productVariantRepository.findByColorAndSize(request.getColor(), request.getSize());
            existingVariant.setStock(existingVariant.getStock() + request.getStock());
            this.productVariantRepository.save(existingVariant);
            return this.modelMapper.map(existingVariant, CreateProductVariantResponse.class);
        }
        else{
            ProductVariantEntity productVariant = new ProductVariantEntity();
            productVariant.setSize(request.getSize());
            productVariant.setColor(request.getColor());
            productVariant.setStock(request.getStock());
            productVariant.setSku(request.getSku());
            this.productVariantRepository.save(productVariant);
        
            return this.modelMapper.map(productVariant, CreateProductVariantResponse.class);
        }
    }
}
