package com.example.sneaker_store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sneaker_store.model.request.product.CreateProductRequest;
import com.example.sneaker_store.model.response.product.CreateProductResponse;
import com.example.sneaker_store.service.ProductService;
import com.example.sneaker_store.util.ApiMessage;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-CONTROLLER")
@RequestMapping("/product/v1")
public class ProductController {
    private final ProductService productService;
    
    @PostMapping("products")
    @Operation(summary = "Create a new product", description = "Creates a new product with the provided details")
    @ApiMessage(message = "Product created successfully")
    public ResponseEntity<CreateProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request) {
        log.info("Received request to create product");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.createProduct(request));
    }
    
}
