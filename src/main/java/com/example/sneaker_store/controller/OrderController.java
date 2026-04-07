package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.order.CreateOrderRequest;
import com.example.sneaker_store.model.response.order.CreateOrderResponse;
import com.example.sneaker_store.service.OrderService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j(topic = "ORDER-CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("/order/v1")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    @Operation(summary = "Create a new order", description = "Create a new order")
    @ApiMessage(message = "Order created successfully")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody @Valid CreateOrderRequest request,
            @CookieValue(name = "guestId", required = false) String guestId) {
        log.info("Received request to create order");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.createOrder(request, guestId));
    }
}
