package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.productImage.CreateProductImageRequest;
import com.example.sneaker_store.model.response.productImage.CreateProductImageResponse;
import com.example.sneaker_store.service.ProductImageService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-IMAGE-CONTROLLER")
@RequestMapping("/product-image/v1")
public class ProductImageController {
    private final ProductImageService productImageService;

    @PostMapping("/product-images")
    @ApiMessage(message = "Tạo product image thành công")
    @Operation(summary = "Create product image", description = "Tạo mới product image")
    public ResponseEntity<CreateProductImageResponse> create(@RequestBody @Valid CreateProductImageRequest req) {
        log.info("CREATE PRODUCT IMAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productImageService.createProductImage(req));
    }
}
