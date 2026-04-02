package com.example.sneaker_store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sneaker_store.model.request.discount.CreateDiscountRequest;
import com.example.sneaker_store.model.response.discount.CreateDiscountResponse;
import com.example.sneaker_store.service.DiscountService;
import com.example.sneaker_store.util.ApiMessage;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
        log.info("Received request to create discount: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.discountService.createDiscount(request));
    }
    
}
