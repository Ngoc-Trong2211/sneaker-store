package com.example.sneaker_store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sneaker_store.model.request.discount.CreateDiscountRequest;
import com.example.sneaker_store.model.request.discount.UpdateDiscountRequest;
import com.example.sneaker_store.model.response.discount.CreateDiscountResponse;
import com.example.sneaker_store.model.response.discount.GetDiscountResponse;
import com.example.sneaker_store.model.response.discount.UpdateDiscountResponse;
import com.example.sneaker_store.service.DiscountService;
import com.example.sneaker_store.util.ApiMessage;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "DISCOUNT-CONTROLLER")
@RequestMapping("/discount/v1")
public class DiscountController {
    private final DiscountService discountService;

    @PostMapping("/discounts")
    @Operation(summary = "Create a new discount", description = "Create a new discount with the provided details")
    @ApiMessage(message = "Discount created successfully")
    public ResponseEntity<CreateDiscountResponse> create(@RequestBody @Valid CreateDiscountRequest request){ 
        log.info("Received request to create discount");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.discountService.createDiscount(request));
    }
    
    @PutMapping("/discounts")
    @Operation(summary = "Update an existing discount", description = "Update an existing discount with the provided details")
    @ApiMessage(message = "Discount updated successfully")
    public ResponseEntity<UpdateDiscountResponse> update(@RequestBody @Valid UpdateDiscountRequest request) {
        log.info("Received request to update discount");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.discountService.updateDiscount(request));
    }

    @GetMapping("/discounts/{id}")
    @Operation(summary = "Get discount by ID", description = "Retrieve a discount by its ID")
    @ApiMessage(message = "Discount retrieved successfully")
    public ResponseEntity<GetDiscountResponse.Discount> getDiscountById(@PathVariable String id) {
        return ResponseEntity.ok(this.discountService.getDiscountById(id));
    }
    
}
