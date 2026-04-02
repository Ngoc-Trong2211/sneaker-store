package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.service.ProductService;
import com.example.sneaker_store.service.specification.ProductSpecification;
import com.example.sneaker_store.util.enumEntity.ProductStatus;
import com.example.sneaker_store.util.exception.NameExistsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.model.request.product.CreateProductRequest;
import com.example.sneaker_store.model.request.product.SpecificationProductRequest;
import com.example.sneaker_store.model.request.product.UpdateProductRequest;
import com.example.sneaker_store.model.response.product.CreateProductResponse;
import com.example.sneaker_store.model.response.product.GetProductByIdResponse;
import com.example.sneaker_store.model.response.product.GetProductResponse;
import com.example.sneaker_store.model.response.product.UpdateProductResponse;
import com.example.sneaker_store.repository.ProductRepository;

@Service
@Slf4j(topic = "PRODUCT-SERVICE")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public CreateProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            log.warn("Product with name '{}' already exists", request.getName());
            throw new NameExistsException("Product with the same name already exists");
        }
        ProductEntity product = new ProductEntity();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStatus(ProductStatus.ACTIVE);
        this.productRepository.save(product);
        return this.modelMapper.map(product, CreateProductResponse.class);
    }

    @Override
    public UpdateProductResponse updateProduct(UpdateProductRequest request) {
        ProductEntity product = this.productRepository.findById(request.getId()).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", request.getId());
            return new RuntimeException("Product not found");
        });
        if (productRepository.existsByName(request.getName())) {
            log.warn("Product with name '{}' already exists", request.getName());
            throw new NameExistsException("Product with the same name already exists");
        }
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        this.productRepository.save(product);
        return this.modelMapper.map(product, UpdateProductResponse.class);
    }

    @Override
    public GetProductResponse getProducts(Pageable pageable, SpecificationProductRequest request) {
        Specification<ProductEntity> specification = ProductSpecification.specProduct(request);
        Page<ProductEntity> productPage = this.productRepository.findAll(specification, pageable);

        GetProductResponse response = new GetProductResponse();
        response.setPage(this.modelMapper.map(response, GetProductResponse.DataPage.class));
        response.setProducts(productPage.map(product -> this.modelMapper.map(product, GetProductResponse.Product.class)).getContent());
        return response;
    }

    @Override
    public GetProductByIdResponse getProductById(String id) {
        ProductEntity product = this.productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", id);
            return new RuntimeException("Product not found");
        });
        return this.modelMapper.map(product, GetProductByIdResponse.class);
    }

    @Override
    public void updateStatusProduct(String id, ProductStatus status) {
        ProductEntity product = this.productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", id);
            return new RuntimeException("Product not found");
        });
        product.setStatus(status);
        this.productRepository.save(product);
    }

    @Override
    public void deleteProduct(String id) {
        ProductEntity product = this.productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", id);
            return new RuntimeException("Product not found");
        });
        product.setStatus(ProductStatus.DELETED);
        this.productRepository.save(product);
    }
}
