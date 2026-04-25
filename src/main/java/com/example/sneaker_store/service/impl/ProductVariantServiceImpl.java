package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.service.ProductVariantService;
import com.example.sneaker_store.specification.ProductVariantSpecification;
import com.example.sneaker_store.util.SkuGenerator;
import com.example.sneaker_store.util.enumEntity.VariantStatus;

import jakarta.transaction.Transactional;

import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.model.ProductVariantEntity;
import com.example.sneaker_store.dto.request.productVariant.CreateProductVariantRequest;
import com.example.sneaker_store.dto.request.productVariant.SpecificationProductVariantRequest;
import com.example.sneaker_store.dto.request.productVariant.UpdateProductVariantRequest;
import com.example.sneaker_store.dto.response.productVariant.CreateProductVariantResponse;
import com.example.sneaker_store.dto.response.productVariant.GetProductVariantResponse;
import com.example.sneaker_store.dto.response.productVariant.UpdateProductVariantResponse;
import com.example.sneaker_store.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CreateProductVariantResponse createProductVariant(CreateProductVariantRequest request) {
        log.info("Creating product variant with size: {}, color: {}, stock: {}",
                request.getSize(), request.getColor(), request.getStock());
        ProductEntity product = this.productRepository.findByName(request.getProductName()).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", request.getProductName());
            return new RuntimeException("Product not found");
        });
        if (this.productVariantRepository.findByColorAndSizeAndProductId(request.getColor(), request.getSize(), product.getId()) != null) {
            log.warn("Product variant with size: {} and color: {} already exists", request.getSize(), request.getColor());
            ProductVariantEntity existingVariant = this.productVariantRepository.findByColorAndSizeAndProductId(request.getColor(), request.getSize(), product.getId());
            existingVariant.setStock(existingVariant.getStock() + request.getStock());
            this.productVariantRepository.save(existingVariant);
            product.setQuantity(product.getQuantity() + existingVariant.getStock());
            this.productRepository.save(product);
            return this.modelMapper.map(existingVariant, CreateProductVariantResponse.class);
        }
        else{
            ProductVariantEntity productVariant = new ProductVariantEntity();
            productVariant.setSize(request.getSize());
            productVariant.setColor(request.getColor());
            productVariant.setStock(request.getStock());
            productVariant.setProduct(product);
            productVariant.setSku(SkuGenerator.generate(product.getBrand().getName(), product.getName(), request.getColor(), request.getSize()));
            this.productVariantRepository.save(productVariant);
            product.setQuantity(product.getQuantity() + productVariant.getStock());
            this.productRepository.save(product);
            return this.modelMapper.map(productVariant, CreateProductVariantResponse.class);
        }
    }

    @Override
    @Transactional
    public UpdateProductVariantResponse updateProductVariant(UpdateProductVariantRequest request) {
        log.info("Updating product variant with id: {}, size: {}, color: {}, stock: {}",
                request.getId(), request.getSize(), request.getColor(), request.getStock());  
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(request.getId()).orElse(null);
        ProductEntity product = this.productRepository.findById(request.getProductId()).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", request.getProductId());
            return new RuntimeException("Product not found");
        });
        if (existingVariant == null) {
            log.warn("Product variant with id: {} not found", request.getId());
            throw new RuntimeException("Product variant not found");
        } 
        if (this.productVariantRepository.findByColorAndSizeAndProductId(request.getColor(), request.getSize(), request.getProductId()) != null) {
            log.warn("Product variant with size: {} and color: {} already exists", request.getSize(), request.getColor());
            ProductVariantEntity productVariant = this.productVariantRepository.findByColorAndSizeAndProductId(request.getColor(), request.getSize(), request.getProductId());
            existingVariant.setStock(productVariant.getStock() + request.getStock());
            this.productVariantRepository.save(productVariant);
            product.setQuantity(product.getQuantity() + existingVariant.getStock());
            this.productRepository.save(product);
            return this.modelMapper.map(productVariant, UpdateProductVariantResponse.class);
        }
        else{
            existingVariant.setSize(request.getSize());
            existingVariant.setColor(request.getColor());
            existingVariant.setStock(request.getStock());
            existingVariant.setProduct(product);
            existingVariant.setSku(SkuGenerator.generate(product.getBrand().getName(), product.getName(), request.getColor(), request.getSize()));
            this.productVariantRepository.save(existingVariant);
            product.setQuantity(product.getQuantity() + existingVariant.getStock());
            this.productRepository.save(product);
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
            productVariant -> {
                GetProductVariantResponse.ProductVariant resVariant = this.modelMapper.map(
                        productVariant, GetProductVariantResponse.ProductVariant.class);
                resVariant.setProductName(productVariant.getProduct().getName());
                resVariant.setBrandName(productVariant.getProduct().getBrand().getName());
                return resVariant;
            }).toList());
        return response;
    }

    @Override
    @Transactional
    public void deleteProductVariant(String id) {
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(id).orElse(null);
        if (existingVariant == null) {
            log.warn("Product variant with id: {} not found", id);
            throw new RuntimeException("Product variant not found");
        }
        ProductEntity product = this.productRepository.findById(existingVariant.getProduct().getId()).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", existingVariant.getProduct().getId());
            return new RuntimeException("Product not found");
        });
        product.setQuantity(product.getQuantity() - existingVariant.getStock());
        this.productRepository.save(product);
        existingVariant.setStatus(VariantStatus.DELETED);
        this.productVariantRepository.save(existingVariant);
    }  

    @Override
    public void updateProductVariantStatus(String id, VariantStatus status) {
        if (status == VariantStatus.DELETED) {
            this.deleteProductVariant(id);
            return;
        }
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(id).orElse(null);
        if (existingVariant == null) {
            log.warn("Product variant with id: {} not found", id);
            throw new RuntimeException("Product variant not found");
        }
        if (status == VariantStatus.SOLD_OUT && existingVariant.getStock() > 0) throw new RuntimeException("Stock > 0");
        existingVariant.setStatus(status);
        this.productVariantRepository.save(existingVariant);
    }
}
