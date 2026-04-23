package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.request.productImage.CreateProductImageRequest;
import com.example.sneaker_store.dto.request.productImage.UpdateProductImageRequest;
import com.example.sneaker_store.dto.response.productImage.CreateProductImageResponse;
import com.example.sneaker_store.dto.response.productImage.GetProductImageResponse;
import com.example.sneaker_store.dto.response.productImage.UpdateProductImageResponse;
import com.example.sneaker_store.service.ProductImageService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-IMAGE-CONTROLLER")
@RequestMapping("/product-image/v1")
public class ProductImageController {
    private final ProductImageService productImageService;

    @PostMapping(value = "/product-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage(message = "Tạo product image thành công")
    @Operation(summary = "Create product image", description = "Tạo mới product image")
    public ResponseEntity<List<CreateProductImageResponse>> create(@RequestPart("files") MultipartFile[] files) {
        log.info("CREATE PRODUCT IMAGE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productImageService.createProductImage(files));
    }

    @PutMapping("/product-images")
    @ApiMessage(message = "Update product image thành công")
    @Operation(summary = "Update product image", description = "Update product image")
    public ResponseEntity<UpdateProductImageResponse> update(@RequestBody @Valid UpdateProductImageRequest req) {
        log.info("UPDATE PRODUCT IMAGE");
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(this.productImageService.updateProductImage(req));
    }

    @DeleteMapping("/product-images/{id}")
    @ApiMessage(message = "Delete product image thành công")
    @Operation(summary = "Delete product image", description = "Delete product image")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE PRODUCT IMAGE");
        this.productImageService.deleteProductImage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product-images/{productId}")
    @ApiMessage(message = "Lấy product image thành công")
    @Operation(summary = "Get product image by product id", description = "Lấy product image theo product id")
    public ResponseEntity<GetProductImageResponse> getByProductId(@PathVariable String productId) {
        log.info("GET PRODUCT IMAGE BY ID");
        return ResponseEntity.ok(this.productImageService.getProductImageById(productId));
    }
}
