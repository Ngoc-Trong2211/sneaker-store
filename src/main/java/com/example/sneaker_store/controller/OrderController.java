package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.request.order.CreateOrderRequest;
import com.example.sneaker_store.dto.request.order.SpecificationOrderRequest;
import com.example.sneaker_store.dto.response.order.CreateOrderResponse;
import com.example.sneaker_store.dto.response.order.GetOrderResponse;
import com.example.sneaker_store.service.OrderService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
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
            @CookieValue(value = "guest_id", required = false) String guestId) {
        log.info("Received request to create order");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderService.createOrder(request, guestId));
    }

    @GetMapping("/orders")
    @Operation(summary = "Get orders with pagination and filtering",
            description = "Retrieve a paginated list of orders based on filtering criteria")
    @ApiMessage(message = "orders retrieved successfully")
    public ResponseEntity<GetOrderResponse> getOrder(@ParameterObject Pageable pageable, SpecificationOrderRequest request) {
        log.info("Received request to get discounts with filters");
        return ResponseEntity.ok(this.orderService.getOrder(pageable, request));
    }

    @PutMapping("/orders/{id}")
    @Operation(summary = "Update order", description = "Update order")
    @ApiMessage(message = "Order updated successfully")
    public ResponseEntity<Void> updateOrder(@PathVariable("id") String id, @RequestParam String status) {
        this.orderService.updateStatus(id, status);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
