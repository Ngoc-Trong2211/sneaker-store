package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.service.ProductVariantService;
import com.example.sneaker_store.specification.ProductVariantSpecification;
import com.example.sneaker_store.util.SkuGenerator;
import com.example.sneaker_store.util.enumEntity.ProductStatus;
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
        if (product.getStatus() != ProductStatus.ACTIVE) throw new RuntimeException("San pham khong hoat dong");
        if (request.getStock() <= 0) throw new RuntimeException("Stock must be > 0");
        ProductVariantEntity existingVariant =
                productVariantRepository.findByColorAndSizeAndProductId(
                        request.getColor(),
                        request.getSize(),
                        product.getId()
                );
        if (existingVariant != null) {
            existingVariant.setStock(existingVariant.getStock() + request.getStock());
            productVariantRepository.save(existingVariant);
            product.setQuantity(product.getQuantity() + request.getStock());
            productRepository.save(product);
            return modelMapper.map(existingVariant, CreateProductVariantResponse.class);
        }

        ProductVariantEntity variant = new ProductVariantEntity();
        variant.setSize(request.getSize());
        variant.setColor(request.getColor());
        variant.setStock(request.getStock());
        variant.setProduct(product);
        variant.setSku(
                SkuGenerator.generate(
                        product.getBrand().getName(),
                        product.getName(),
                        request.getColor(),
                        request.getSize()
                ) + "-" + System.currentTimeMillis()
        );
        productVariantRepository.save(variant);
        product.setQuantity(product.getQuantity() + variant.getStock());
        productRepository.save(product);
        return modelMapper.map(variant, CreateProductVariantResponse.class);
    }

    @Override
    @Transactional
    public UpdateProductVariantResponse updateProductVariant(UpdateProductVariantRequest request) {
        ProductVariantEntity variant = productVariantRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        ProductEntity product = productRepository.findByName(request.getProductName())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getStatus() != ProductStatus.ACTIVE)
            throw new RuntimeException("San pham khong hoat dong");
        if (request.getStock() <= 0)
            throw new RuntimeException("Stock must be > 0");
        ProductVariantEntity duplicate = productVariantRepository
                .findByColorAndSizeAndProductId(request.getColor(), request.getSize(), product.getId());

        if (duplicate != null && !duplicate.getId().equals(variant.getId())) {
            throw new RuntimeException("Variant already exists");
        }
        int diff = request.getStock() - variant.getStock();
        if (!variant.getProduct().getId().equals(product.getId())) {
            ProductEntity oldProduct = variant.getProduct();
            oldProduct.setQuantity(oldProduct.getQuantity() - variant.getStock());
            productRepository.save(oldProduct);

            product.setQuantity(product.getQuantity() + request.getStock());
        } else {
            product.setQuantity(product.getQuantity() + diff);
        }
        variant.setSize(request.getSize());
        variant.setColor(request.getColor());
        variant.setStock(request.getStock());
        variant.setProduct(product);
        variant.setSku(SkuGenerator.generate(
                product.getBrand().getName(),
                product.getName(),
                request.getColor(),
                request.getSize()
        ));
        productVariantRepository.save(variant);
        productRepository.save(product);

        return modelMapper.map(variant, UpdateProductVariantResponse.class);
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
        existingVariant.setStatus(VariantStatus.DELETED);
        this.productVariantRepository.save(existingVariant);
    }  

    @Override
    public void updateProductVariantStatus(String id, String status) {
        if (VariantStatus.valueOf(status) == VariantStatus.DELETED) {
            this.deleteProductVariant(id);
            return;
        }
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(id).orElse(null);
        if (existingVariant == null) {
            log.warn("Product variant with id: {} not found", id);
            throw new RuntimeException("Product variant not found");
        }
        if (VariantStatus.valueOf(status) == VariantStatus.SOLD_OUT && existingVariant.getStock() > 0) throw new RuntimeException("Stock > 0");
        existingVariant.setStatus(VariantStatus.valueOf(status));
        this.productVariantRepository.save(existingVariant);
    }
}
