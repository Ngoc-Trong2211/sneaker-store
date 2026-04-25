package com.example.sneaker_store.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sneaker_store.dto.request.productVariant.CreateProductVariantRequest;
import com.example.sneaker_store.dto.request.productVariant.SpecificationProductVariantRequest;
import com.example.sneaker_store.dto.request.productVariant.UpdateProductVariantRequest;
import com.example.sneaker_store.dto.response.productVariant.CreateProductVariantResponse;
import com.example.sneaker_store.dto.response.productVariant.GetProductVariantResponse;
import com.example.sneaker_store.dto.response.productVariant.UpdateProductVariantResponse;
import com.example.sneaker_store.service.ProductVariantService;
import com.example.sneaker_store.util.ApiMessage;
import com.example.sneaker_store.util.enumEntity.VariantStatus;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-VARIANT-CONTROLLER")
@RequestMapping("/product-variant/v1")
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @PostMapping("/product-variants")
    @Operation(summary = "Create a new product variant", description = "Create a new product variant with the specified size, color, stock, and SKU.")
    @ApiMessage(message = "Product variant created successfully")
    public ResponseEntity<CreateProductVariantResponse> createProductVariant(
            @RequestBody @Valid CreateProductVariantRequest request) {
        log.info("Received request to create product variant with size: {}, color: {}, stock: {}",
                request.getSize(), request.getColor(), request.getStock());
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productVariantService.createProductVariant(request));
    }

    @PutMapping("/product-variants")
    @Operation(summary = "Update an existing product variant", description = "Update an existing product variant with the specified ID, size, color, stock, and SKU.")
    @ApiMessage(message = "Product variant updated successfully")
    public ResponseEntity<UpdateProductVariantResponse> updateProductVariant(
            @RequestBody @Valid UpdateProductVariantRequest request) {
        log.info("Received request to update product variant with id: {}, size: {}, color: {}, stock: {}",
                request.getId(), request.getSize(), request.getColor(), request.getStock());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(this.productVariantService.updateProductVariant(request));
    }

    @GetMapping("product-variants")
    @Operation(summary = "Get product variants with pagination and filtering", description = "Get a paginated list of product variants filtered by size and color.")
    @ApiMessage(message = "Product variants retrieved successfully")
    public ResponseEntity<GetProductVariantResponse> getMethodName(Pageable pageable,
                                                                   SpecificationProductVariantRequest request) {
        return ResponseEntity.ok(this.productVariantService.getProductVariant(pageable, request));
    }

    @PatchMapping("/product-variants/{id}")
    @Operation(summary = "Delete a product variant", description = "Delete a product variant with the specified ID.")
    @ApiMessage(message = "Product variant deleted successfully")
    public ResponseEntity<Void> deleteProductVariant(@PathVariable String id) {
        this.productVariantService.deleteProductVariant(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/product-variants/{id}/status")
    @Operation(summary = "Update the status of a product variant", description = "Update the status of a product variant with the specified ID.")
    @ApiMessage(message = "Product variant status updated successfully")
    public ResponseEntity<Void> updateProductVariantStatus(@PathVariable String id, @RequestParam VariantStatus status) {
        this.productVariantService.updateProductVariantStatus(id, status);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}