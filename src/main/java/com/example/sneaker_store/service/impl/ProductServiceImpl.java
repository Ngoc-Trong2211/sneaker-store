package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.service.BrandService;
import com.example.sneaker_store.service.CategoryService;
import com.example.sneaker_store.service.ProductService;
import com.example.sneaker_store.specification.ProductSpecification;
import com.example.sneaker_store.util.enumEntity.ProductStatus;
import com.example.sneaker_store.util.exception.NameExistsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.sneaker_store.model.BrandEntity;
import com.example.sneaker_store.model.CategoryEntity;
import com.example.sneaker_store.model.ProductEntity;
import com.example.sneaker_store.model.ProductImageEntity;
import com.example.sneaker_store.dto.request.product.CreateProductRequest;
import com.example.sneaker_store.dto.request.product.SpecificationProductRequest;
import com.example.sneaker_store.dto.request.product.UpdateProductRequest;
import com.example.sneaker_store.dto.response.product.CreateProductResponse;
import com.example.sneaker_store.dto.response.product.GetProductByIdResponse;
import com.example.sneaker_store.dto.response.product.GetProductResponse;
import com.example.sneaker_store.dto.response.product.UpdateProductResponse;
import com.example.sneaker_store.repository.ProductRepository;

@Service
@Slf4j(topic = "PRODUCT-SERVICE")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final BrandService brandService;
    private final CategoryService categoryService;

    @Override
    public CreateProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            log.warn("Product with name '{}' already exists", request.getName());
            throw new NameExistsException("Product with the same name already exists");
        }
        BrandEntity brand = this.brandService.findById(request.getBrandId());
        CategoryEntity category = this.categoryService.findById(request.getCategoryId());
        ProductEntity product = new ProductEntity();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStatus(ProductStatus.ACTIVE);
        product.setBrand(brand);
        product.setCategory(category);
        this.productRepository.save(product);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            if (request.getImages().size() > 6) {
                log.warn("Too many images provided for product '{}'", request.getName());
                throw new IllegalArgumentException("Maximum 6 images allowed");
            }
            else{
                List<ProductImageEntity> productImages = IntStream.range(0, request.getImages().size())
                    .mapToObj(i -> {
                        ProductImageEntity image = new ProductImageEntity();
                        image.setImageURL(request.getImages().get(i));
                        image.setMain(i == 0);
                        image.setProduct(product);
                        return image;
                    })
                    .collect(Collectors.toList());
                product.setImages(productImages);
            }
        }
        this.productRepository.save(product);

        CreateProductResponse res = this.modelMapper.map(product, CreateProductResponse.class);
        res.setBrandName(product.getBrand().getName());
        res.setCategoryName(product.getCategory().getName());
        return res;
    }

    @Override
    public UpdateProductResponse updateProduct(UpdateProductRequest request) {
        ProductEntity product = this.productRepository.findById(request.getId()).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", request.getId());
            return new RuntimeException("Product not found");
        });
        if (productRepository.existsByNameAndIdNot(request.getName(), request.getId())) {
            log.warn("Product with name '{}' already exists", request.getName());
            throw new NameExistsException("Product with the same name already exists");
        }
        BrandEntity brand = this.brandService.findById(request.getBrandId());
        CategoryEntity category = this.categoryService.findById(request.getCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(brand);
        product.setCategory(category);
        this.productRepository.save(product);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            if (request.getImages().size() > 6) {
                log.warn("Too many images provided for product '{}'", request.getName());
                throw new IllegalArgumentException("Maximum 6 images allowed");
            }
            else{
                List<ProductImageEntity> productImages = IntStream.range(0, request.getImages().size())
                    .mapToObj(i -> {
                        ProductImageEntity image = new ProductImageEntity();
                        image.setImageURL(request.getImages().get(i));
                        image.setMain(i == 0);
                        image.setProduct(product);
                        return image;
                    })
                    .collect(Collectors.toList());
                product.setImages(productImages);
            }
        }
        this.productRepository.save(product);
        
        UpdateProductResponse res = this.modelMapper.map(product, UpdateProductResponse.class);
        res.setBrandName(product.getBrand().getName());
        res.setCategoryName(product.getCategory().getName());
        return res;
    }

    @Override
    public GetProductResponse getProducts(Pageable pageable, SpecificationProductRequest request) {
        Specification<ProductEntity> specification = ProductSpecification.specProduct(request);
        Page<ProductEntity> productPage = this.productRepository.findAll(specification, pageable);

        GetProductResponse response = new GetProductResponse();
        response.setPage(this.modelMapper.map(response, GetProductResponse.DataPage.class));
        response.setProducts(productPage.map(product -> {
            GetProductResponse.Product prod = this.modelMapper.map(product, GetProductResponse.Product.class);
            prod.setBrandName(product.getBrand().getName());
            prod.setCategoryName(product.getCategory().getName());
            return prod;
        }).getContent());
        return response;
    }

    @Override
    public GetProductByIdResponse getProductById(String id) {
        ProductEntity product = this.productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", id);
            return new RuntimeException("Product not found");
        });
        GetProductByIdResponse res = this.modelMapper.map(product, GetProductByIdResponse.class);
        res.setBrandName(product.getBrand().getName());
        res.setCategoryName(product.getCategory().getName());
        return res;
    }

    @Override
    public void updateStatusProduct(String id, ProductStatus status) {
        ProductEntity product = this.productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", id);
            return new RuntimeException("Product not found");
        });
        if (status == ProductStatus.SOLD_OUT && product.getQuantity() > 0) throw new RuntimeException("Quantity > 0");
        if (status == ProductStatus.ACTIVE && product.getQuantity() == 0) throw new RuntimeException("Quantity = 0");
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
        product.getImages().clear();
        this.productRepository.save(product);
    }
}
