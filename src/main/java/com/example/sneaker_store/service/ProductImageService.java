package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.productImage.CreateProductImageRequest;
import com.example.sneaker_store.dto.request.productImage.UpdateProductImageRequest;
import com.example.sneaker_store.dto.response.productImage.CreateProductImageResponse;
import com.example.sneaker_store.dto.response.productImage.GetProductImageResponse;
import com.example.sneaker_store.dto.response.productImage.UpdateProductImageResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    List<CreateProductImageResponse> createProductImage(MultipartFile[] files);
    UpdateProductImageResponse updateProductImage(UpdateProductImageRequest req);
    void deleteProductImage(Long id);
    GetProductImageResponse getProductImageById(String productId);
}
