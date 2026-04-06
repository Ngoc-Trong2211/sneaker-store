package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.cartItem.CreateCartItemRequest;
import com.example.sneaker_store.model.response.cartItem.CreateCartItemResponse;
import com.example.sneaker_store.service.CartItemService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j(topic = "CART-ITEM-CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("/cart-item/v1")
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping("/cart-items")
    @Operation(summary = "Create a new cart item", description = "Create a new cart item")
    @ApiMessage(message = "Cart item created successfully")
    public ResponseEntity<CreateCartItemResponse> createCartItem(
            @RequestBody @Valid CreateCartItemRequest request) {
        log.info("Received request to create cart item");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.cartItemService.createCartItem(request));
    }
}
