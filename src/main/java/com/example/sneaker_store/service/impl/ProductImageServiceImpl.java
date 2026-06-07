package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.response.UploadFileResponse;
import com.example.sneaker_store.model.ProductImageEntity;
import com.example.sneaker_store.dto.request.productImage.UpdateProductImageRequest;
import com.example.sneaker_store.dto.response.productImage.CreateProductImageResponse;
import com.example.sneaker_store.dto.response.productImage.GetProductImageResponse;
import com.example.sneaker_store.dto.response.productImage.UpdateProductImageResponse;
import com.example.sneaker_store.repository.ProductImageRepository;
import com.example.sneaker_store.service.FileService;
import com.example.sneaker_store.service.ProductImageService;
import com.example.sneaker_store.util.exception.user.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j(topic = "PRODUCT-IMAGE-SERVICE")
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    private List<UploadFileResponse> uploadFileResponses(MultipartFile[] files){
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("Danh sách file rỗng");
        }
        List<UploadFileResponse> res = new ArrayList<>();
        for (MultipartFile file : files){
            res.add(this.fileService.uploadFile(file, "product"));
        }
        return res;
    }

    @Override
    @Transactional
    public List<CreateProductImageResponse> createProductImage(MultipartFile[] files) {
        List<CreateProductImageResponse> listRes = new ArrayList<>();
        List<UploadFileResponse> uploadMultiFile = uploadFileResponses(files);
        for (UploadFileResponse fileRes : uploadMultiFile){
            ProductImageEntity img = new ProductImageEntity();
            img.setImageURL(fileRes.getUrl());
            img.setPublicId(fileRes.getPublicId());
            img.setMain(false);
            this.productImageRepository.save(img);
            listRes.add(this.modelMapper.map(img, CreateProductImageResponse.class));
        }
        return listRes;
    }

    @Override
    public List<String> updateProductImage(MultipartFile[] files, List<String> oldFiles) {
        List<String> listRes = new ArrayList<>(
                oldFiles != null ? oldFiles : new ArrayList<>()
        );
        if (files == null || files.length == 0) {
            return listRes;
        }
        List<UploadFileResponse> uploadMultiFile = uploadFileResponses(files);
        for (UploadFileResponse fileRes : uploadMultiFile) {
            ProductImageEntity img = new ProductImageEntity();
            img.setImageURL(fileRes.getUrl());
            img.setPublicId(fileRes.getPublicId());
            img.setMain(false);
            this.productImageRepository.save(img);
            listRes.add(fileRes.getUrl());
        }
        return listRes;
    }

    @Override
    public void deleteProductImage(Long id) {
        ProductImageEntity img = this.productImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy image"));
        fileService.deleteFile(img.getPublicId());
        this.productImageRepository.delete(img);
    }

    @Override
    public GetProductImageResponse getProductImageById(String productId) {
        List<ProductImageEntity> imgList = this.productImageRepository.findByVariantId(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy image"));
        GetProductImageResponse response = new GetProductImageResponse();
        response.setImages(imgList.stream().map(img -> this.modelMapper.map(img, GetProductImageResponse.ProductImage.class)).toList());
        return response;
    }
}
