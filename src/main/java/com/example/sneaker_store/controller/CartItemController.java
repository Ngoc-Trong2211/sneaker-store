package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.cartItem.CreateCartItemRequest;
import com.example.sneaker_store.model.response.cartItem.CreateCartItemResponse;
import com.example.sneaker_store.service.CartItemService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RestController
@Slf4j(topic = "CART-ITEM-CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("/cart-item/v1")
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping("/cart-items")
    @Operation(summary = "Add cart item", description = "Add cart item")
    @ApiMessage(message = "Add cart item successfully")
    public ResponseEntity<CreateCartItemResponse> createCartItem(
            @RequestBody @Valid CreateCartItemRequest request,
            @CookieValue(value = "guestId", required = false) String guestId,
            HttpServletResponse response) {
        log.info("Received request to add cart item");
        if (guestId == null || guestId.isBlank()) {
            guestId = UUID.randomUUID().toString();
            ResponseCookie cookie = ResponseCookie
                    .from("guestId", guestId)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(60 * 60 * 24 * 30)
                    .sameSite("Lax")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        request.setGuestId(guestId);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.cartItemService.addToCart(request));
    }

    @PatchMapping("/cart-items/{id}")
    @Operation(summary = "Delete a cart item", description = "Delete a cart item with the specified ID.")
    @ApiMessage(message = "Cart item deleted successfully")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        this.cartItemService.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }
}
