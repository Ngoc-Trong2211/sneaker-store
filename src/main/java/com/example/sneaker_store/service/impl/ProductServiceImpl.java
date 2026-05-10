package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.*;
import com.example.sneaker_store.repository.ProductImageRepository;
import com.example.sneaker_store.repository.ProductVariantRepository;
import com.example.sneaker_store.service.*;
import com.example.sneaker_store.specification.ProductSpecification;
import com.example.sneaker_store.util.enumEntity.ProductStatus;
import com.example.sneaker_store.util.enumEntity.VariantStatus;
import com.example.sneaker_store.util.exception.NameExistsException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final FileService fileService;
    private final ProductImageService productImageService;
    private final ProductVariantService productVariantService;

    private String formatPriceToResponse(Double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return formatter.format(price);
    }

    @Override
    @Transactional
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
                for (int i=0; i<request.getImages().size(); i++){
                    ProductImageEntity image = this.productImageRepository.findByImageURL(request.getImages().get(i));
                    if (image == null) {
                        throw new RuntimeException("Image not found with URL: " + request.getImages().get(i));
                    }
                    image.setMain(i == 0);
                    image.setProduct(product);
                    System.out.println("save");
                    this.productImageRepository.save(image);
                }
            }
        }

        CreateProductResponse res = this.modelMapper.map(product, CreateProductResponse.class);
        res.setPrice(formatPriceToResponse(request.getPrice()));
        res.setBrandName(product.getBrand().getName());
        res.setCategoryName(product.getCategory().getName());
        return res;
    }

    @Override
    @Transactional
    public UpdateProductResponse updateProduct(UpdateProductRequest request) {
        ProductEntity product = this.productRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (this.productRepository.existsByNameAndIdNot(request.getName(), request.getId())) {
            throw new NameExistsException("Product with the same name already exists");
        }
        BrandEntity brand = this.brandService.findById(request.getBrandId());
        CategoryEntity category = this.categoryService.findById(request.getCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(Double.parseDouble(request.getPrice().replace(",", "")));
        product.setBrand(brand);
        product.setCategory(category);

        List<String> newImageUrls = request.getImages();
        List<ProductImageEntity> currentImages = product.getImages();
        List<ProductImageEntity> toRemove = currentImages.stream()
                .filter(img -> !newImageUrls.contains(img.getImageURL()))
                .toList();
        for (ProductImageEntity img : toRemove) {
            this.fileService.deleteFile(img.getPublicId());
            currentImages.remove(img);
            this.productImageRepository.delete(img);
        }
        for (int i = 0; i < newImageUrls.size(); i++) {
            String url = newImageUrls.get(i);
            boolean exists = currentImages.stream()
                    .anyMatch(img -> img.getImageURL().equals(url));
            if (!exists) {
                ProductImageEntity image = this.productImageRepository.findByImageURL(url);
                if (image == null) {
                    throw new RuntimeException("Image not found with URL: " + url);
                }
                image.setProduct(product);
                image.setMain(i == 0);
                this.productImageRepository.save(image);
                currentImages.add(image);
            } else {
                int finalI = i;
                currentImages.stream()
                        .filter(img -> img.getImageURL().equals(url))
                        .forEach(img -> img.setMain(finalI == 0));
            }
        }
        this.productRepository.save(product);
        UpdateProductResponse res = this.modelMapper.map(product, UpdateProductResponse.class);
        res.setPrice(formatPriceToResponse(Double.parseDouble(request.getPrice().replace(",", ""))));
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
            prod.setPrice(formatPriceToResponse(product.getPrice()));
            prod.setBrandName(product.getBrand().getName());
            prod.setSlugCategory(product.getCategory().getSlug());
            product.getImages().stream()
                    .filter(ProductImageEntity::isMain)
                    .findFirst()
                    .ifPresent(image -> prod.setImage(image.getImageURL()));
            return prod;
        }).getContent());
        return response;
    }

    @Override
    public GetProductByIdResponse getProductById(String slug) {
        ProductEntity product = this.productRepository.findBySlug(slug).orElseThrow(() -> {
            log.warn("Product with slug '{}' not found", slug);
            return new RuntimeException("Product not found");
        });
        GetProductByIdResponse res = this.modelMapper.map(product, GetProductByIdResponse.class);
        res.setPrice(formatPriceToResponse(product.getPrice()));
        res.setBrandId(product.getBrand().getId());
        res.setCategoryId(product.getCategory().getId());
        res.setCategorySlug(product.getCategory().getSlug());
        List<GetProductByIdResponse.ProductImage> listResImg = new ArrayList<>();
        for (ProductImageEntity img : product.getImages()){
            GetProductByIdResponse.ProductImage imgRes = new GetProductByIdResponse.ProductImage();
            imgRes.setUrl(img.getImageURL());
            imgRes.setMain(img.isMain());
            listResImg.add(imgRes);
        }
        res.setImages(listResImg);
        res.setBrandName(product.getBrand().getName());
        res.setCategoryName(product.getCategory().getName());
        return res;
    }

    @Override
    public void updateStatusProduct(String id, String status) {
        ProductEntity product = this.productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", id);
            return new RuntimeException("Product not found");
        });
        if (ProductStatus.valueOf(status) == ProductStatus.SOLD_OUT && product.getQuantity() > 0) throw new RuntimeException("Quantity > 0");
        if (ProductStatus.valueOf(status) == ProductStatus.ACTIVE && product.getQuantity() == 0) throw new RuntimeException("Quantity = 0");
        product.setStatus(ProductStatus.valueOf(status));
        if (ProductStatus.valueOf(status)==(ProductStatus.ACTIVE)){
            Optional<List<ProductVariantEntity>> listVariants= this.productVariantRepository.findByProductId(id);
            if (listVariants.isPresent()){
                for(ProductVariantEntity prdVariant : listVariants.get()){
                    prdVariant.setStatus(VariantStatus.ACTIVE);
                }
            }
        }
        this.productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        ProductEntity product = this.productRepository.findById(id).orElseThrow(() -> {
            log.warn("Product with id '{}' not found", id);
            return new RuntimeException("Product not found");
        });
        if (product.getStatus() == ProductStatus.DELETED) throw new RuntimeException("Product already deleted");
        List<ProductImageEntity> images = this.productImageRepository.findByProductId(id).orElse(List.of());
        for (ProductImageEntity image : images) {
            if (!image.isMain()) {
                this.fileService.deleteFile(image.getPublicId());
                this.productImageService.deleteProductImage(image.getId());
            }
        }
        this.productVariantRepository.deleteSoftProductVariant(product.getId());
        product.setStatus(ProductStatus.DELETED);
        this.productRepository.save(product);
    }
}
