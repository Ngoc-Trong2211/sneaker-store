package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.ProductImageEntity;
import com.example.sneaker_store.dto.request.productImage.CreateProductImageRequest;
import com.example.sneaker_store.dto.request.productImage.UpdateProductImageRequest;
import com.example.sneaker_store.dto.response.productImage.CreateProductImageResponse;
import com.example.sneaker_store.dto.response.productImage.GetProductImageResponse;
import com.example.sneaker_store.dto.response.productImage.UpdateProductImageResponse;
import com.example.sneaker_store.repository.ProductImageRepository;
import com.example.sneaker_store.service.ProductImageService;
import com.example.sneaker_store.util.exception.user.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

    @Override
    public UpdateProductImageResponse updateProductImage(UpdateProductImageRequest req) {
        ProductImageEntity img = this.productImageRepository.findById(req.getId())
                .orElseThrow(() -> new IdInvalidException("Khong ton tai!"));
        img.setImageURL(req.getImageURL());
        img.setMain(req.isMain());
        this.productImageRepository.save(img);
        return this.modelMapper.map(img, UpdateProductImageResponse.class);
    }

    @Override
    public void deleteProductImage(Long id) {
        ProductImageEntity img = this.productImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy image"));
        this.productImageRepository.delete(img);
    }

    @Override
    public GetProductImageResponse getProductImageById(String productId) {
        List<ProductImageEntity> imgList = this.productImageRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy image"));
        GetProductImageResponse response = new GetProductImageResponse();
        response.setImages(imgList.stream().map(img -> this.modelMapper.map(img, GetProductImageResponse.ProductImage.class)).toList());
        return response;
    }
}
