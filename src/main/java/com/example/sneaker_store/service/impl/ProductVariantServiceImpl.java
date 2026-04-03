package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.service.ProductVariantService;
import com.example.sneaker_store.service.specification.ProductVariantSpecification;
import com.example.sneaker_store.util.enumEntity.VariantStatus;
import com.example.sneaker_store.model.ProductVariantEntity;
import com.example.sneaker_store.model.request.productVariant.CreateProductVariantRequest;
import com.example.sneaker_store.model.request.productVariant.SpecificationProductVariantRequest;
import com.example.sneaker_store.model.request.productVariant.UpdateProductVariantRequest;
import com.example.sneaker_store.model.response.productVariant.CreateProductVariantResponse;
import com.example.sneaker_store.model.response.productVariant.GetProductVariantResponse;
import com.example.sneaker_store.model.response.productVariant.UpdateProductVariantResponse;
import com.example.sneaker_store.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public UpdateProductVariantResponse updateProductVariant(UpdateProductVariantRequest request) {
        log.info("Updating product variant with id: {}, size: {}, color: {}, stock: {}, sku: {}",
                request.getId(), request.getSize(), request.getColor(), request.getStock(), request.getSku());  
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(request.getId()).orElse(null);
        if (existingVariant == null) {
            log.warn("Product variant with id: {} not found", request.getId());
            throw new RuntimeException("Product variant not found");
        } 
        if (this.productVariantRepository.findByColorAndSize(request.getColor(), request.getSize()) != null) {
            log.warn("Product variant with size: {} and color: {} already exists", request.getSize(), request.getColor());
            ProductVariantEntity productVariant = this.productVariantRepository.findByColorAndSize(request.getColor(), request.getSize());
            existingVariant.setStock(productVariant.getStock() + request.getStock());
            this.productVariantRepository.save(productVariant);
            return this.modelMapper.map(productVariant, UpdateProductVariantResponse.class);
        }
        else{
            existingVariant.setSize(request.getSize());
            existingVariant.setColor(request.getColor());
            existingVariant.setStock(request.getStock());
            existingVariant.setSku(request.getSku());
            this.productVariantRepository.save(existingVariant);
            return this.modelMapper.map(existingVariant, UpdateProductVariantResponse.class);
        }
    }

    @Override
    public GetProductVariantResponse getProductVariant(Pageable pageable, SpecificationProductVariantRequest request) {
        Specification<ProductVariantEntity> specification = ProductVariantSpecification.specVariant(request);
        Page<ProductVariantEntity> productVariantPage = this.productVariantRepository.findAll(specification, pageable);
        GetProductVariantResponse response = new GetProductVariantResponse();
        response.setPage(this.modelMapper.map(productVariantPage, GetProductVariantResponse.DataPage.class));
        response.setProductVariants(productVariantPage.getContent().stream().map(
            productVariant -> this.modelMapper.map(productVariant, GetProductVariantResponse.ProductVariant.class)).toList());
        return response;
    }

    @Override
    public void deleteProductVariant(String id) {
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(id).orElse(null);
        if (existingVariant == null) {
            log.warn("Product variant with id: {} not found", id);
            throw new RuntimeException("Product variant not found");
        }
        existingVariant.setStatus(VariantStatus.DELETED);
        this.productVariantRepository.save(existingVariant);
    }  

    @Override
    public void updateProductVariantStatus(String id, VariantStatus status) {
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(id).orElse(null);
        if (existingVariant == null) {
            log.warn("Product variant with id: {} not found", id);
            throw new RuntimeException("Product variant not found");
        }
        existingVariant.setStatus(status);
        this.productVariantRepository.save(existingVariant);
    }
}
