package com.example.sneaker_store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sneaker_store.model.request.product.CreateProductRequest;
import com.example.sneaker_store.model.request.product.SpecificationProductRequest;
import com.example.sneaker_store.model.request.product.UpdateProductRequest;
import com.example.sneaker_store.model.response.product.CreateProductResponse;
import com.example.sneaker_store.model.response.product.GetProductByIdResponse;
import com.example.sneaker_store.model.response.product.GetProductResponse;
import com.example.sneaker_store.model.response.product.UpdateProductResponse;
import com.example.sneaker_store.service.ProductService;
import com.example.sneaker_store.util.ApiMessage;
import com.example.sneaker_store.util.enumEntity.ProductStatus;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-CONTROLLER")
@RequestMapping("/product/v1")
public class ProductController {
    private final ProductService productService;
    
    @PostMapping("/products")
    @Operation(summary = "Create a new product", description = "Creates a new product with the provided details")
    @ApiMessage(message = "Product created successfully")
    public ResponseEntity<CreateProductResponse> createProduct(@RequestBody @Valid CreateProductRequest request) {
        log.info("Received request to create product");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.createProduct(request));
    }
    
    @PutMapping("/products")
    @Operation(summary = "Update an existing product", description = "Updates an existing product with the provided details")
    @ApiMessage(message = "Product updated successfully")
    public ResponseEntity<UpdateProductResponse> putMethodName(@RequestBody @Valid UpdateProductRequest request) {
        log.info("Received request to update product with id '{}'", request.getId());
        return ResponseEntity.ok(this.productService.updateProduct(request));
    }

    @GetMapping("/products")
    @Operation(summary = "Get products with pagination and filtering", description = "Retrieves a paginated list of products based on the provided filters")
    @ApiMessage(message = "Products retrieved successfully")
    public ResponseEntity<GetProductResponse> getMethodName(Pageable pageable, SpecificationProductRequest request) {
        log.info("Received request to get products with filters");
        return ResponseEntity.ok(this.productService.getProducts(pageable, request));
    }
    
    @GetMapping("/products/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a product by its unique ID")
    @ApiMessage(message = "Product retrieved successfully")
    public ResponseEntity<GetProductByIdResponse> getProductById(@PathVariable String id) {
        log.info("Received request to get product with id '{}'", id);
        return ResponseEntity.ok(this.productService.getProductById(id));
    }

    @PatchMapping("/products/{id}/status")
    @Operation(summary = "Update product status", description = "Updates the status of a product by its unique ID")
    @ApiMessage(message = "Product status updated successfully")    
    public ResponseEntity<Void> updateStatusProduct(@PathVariable String id, @RequestParam ProductStatus status) {
        log.info("Received request to update status of product with id '{}' to '{}'", id, status);
        this.productService.updateStatusProduct(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/products/{id}/delete")
    @Operation(summary = "Delete a product", description = "Marks a product as deleted by its unique ID")
    @ApiMessage(message = "Product deleted successfully")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        log.info("Received request to delete product with id '{}'", id);
        this.productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
