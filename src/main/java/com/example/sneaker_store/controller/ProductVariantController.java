package com.example.sneaker_store.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sneaker_store.model.request.productVariant.CreateProductVariantRequest;
import com.example.sneaker_store.model.request.productVariant.UpdateProductVariantRequest;
import com.example.sneaker_store.model.response.productVariant.CreateProductVariantResponse;
import com.example.sneaker_store.model.response.productVariant.UpdateProductVariantResponse;
import com.example.sneaker_store.service.ProductVariantService;
import com.example.sneaker_store.util.ApiMessage;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-VARIANT-CONTROLLER")
@RequestMapping("/product-variant/v1")
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @PostMapping("/product-variants")
    @Operation(summary = "Create a new product variant", description = "Create a new product variant with the specified size, color, stock, and SKU.")
    @ApiMessage(message = "Product variant created successfully")
    public ResponseEntity<CreateProductVariantResponse> createProductVariant(@RequestBody @Valid CreateProductVariantRequest request) {
        log.info("Received request to create product variant with size: {}, color: {}, stock: {}, sku: {}",
                request.getSize(), request.getColor(), request.getStock(), request.getSku());
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productVariantService.createProductVariant(request));
    }

    @PutMapping("/product-variants")
    public ResponseEntity<UpdateProductVariantResponse> updateProductVariant(@RequestBody @Valid UpdateProductVariantRequest request) {
        log.info("Received request to update product variant with id: {}, size: {}, color: {}, stock: {}, sku: {}",
                request.getId(), request.getSize(), request.getColor(), request.getStock(), request.getSku());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.productVariantService.updateProductVariant(request));
    }
    
}
