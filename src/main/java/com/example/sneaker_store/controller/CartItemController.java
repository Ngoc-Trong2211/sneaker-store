package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.request.cartItem.CreateCartItemRequest;
import com.example.sneaker_store.dto.request.cartItem.UpdateQuantityRequest;
import com.example.sneaker_store.dto.response.cartItem.CreateCartItemResponse;
import com.example.sneaker_store.dto.response.cartItem.GetCartResponse;
import com.example.sneaker_store.service.CartItemService;
import com.example.sneaker_store.service.CartService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j(topic = "CART-ITEM-CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("/cart-item/v1")
public class CartItemController {
    private final CartItemService cartItemService;
    private final CartService cartService;

    @PostMapping("/cart-items")
    @Operation(summary = "Add cart item", description = "Add cart item")
    @ApiMessage(message = "Add cart item successfully")
    public ResponseEntity<CreateCartItemResponse> createCartItem(
            @RequestBody @Valid CreateCartItemRequest request,
            @CookieValue(value = "guest_id", required = false) String guestId) {
        log.info("Received request to add cart item");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.cartItemService.addToCart(request, guestId));
    }

    @DeleteMapping("/cart-items/{id}")
    @Operation(summary = "Delete a cart item", description = "Delete a cart item with the specified ID.")
    @ApiMessage(message = "Cart item deleted successfully")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        this.cartItemService.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cart-items")
    @Operation(summary = "Get cart items", description = "Get cart items")
    @ApiMessage(message = "Get cart items successfully")
    public ResponseEntity<GetCartResponse> getCartItem(
            @CookieValue(value = "guest_id", required = false) String guestId) {
        return ResponseEntity.ok(this.cartService.getCart(guestId));
    }

    @PutMapping("/cart-items")
    @Operation(summary = "Update quantity cart items", description = "Update quantity cart items")
    @ApiMessage(message = "Update quantity cart items successfully")
    public ResponseEntity<Integer> updateQuantity(@RequestBody @Valid UpdateQuantityRequest req){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.cartItemService.updateQuantity(req));
    }
}
