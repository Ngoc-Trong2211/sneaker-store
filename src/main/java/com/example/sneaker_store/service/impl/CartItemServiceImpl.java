package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.response.cartItem.GetCartResponse;
import com.example.sneaker_store.model.CartEntity;
import com.example.sneaker_store.model.CartItemEntity;
import com.example.sneaker_store.model.ProductVariantEntity;
import com.example.sneaker_store.dto.request.cartItem.CreateCartItemRequest;
import com.example.sneaker_store.dto.response.cartItem.CreateCartItemResponse;
import com.example.sneaker_store.repository.CartItemRepository;
import com.example.sneaker_store.repository.ProductVariantRepository;
import com.example.sneaker_store.service.CartItemService;
import com.example.sneaker_store.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j(topic = "CART-ITEM-SERVICE")
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartService cartService;

    @Override
    public CreateCartItemResponse addToCart(CreateCartItemRequest req, String guestId) {
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(req.getVariantId()).orElseThrow(() -> {
            log.warn("Product variant with id: {} not found", req.getVariantId());
            return new RuntimeException("Product variant not found");
        });
        if (existingVariant.getStock() < 1) throw new RuntimeException("San pham khong du");
        CartEntity cart = this.cartService.createCart(guestId);

        Optional<CartItemEntity> existsCartItem = this.cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), req.getVariantId());

        if (existsCartItem.isPresent()){
            CartItemEntity cartItem = existsCartItem.get();
            cartItem.setSize(req.getSize());
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            this.cartItemRepository.save(cartItem);

            CreateCartItemResponse cartItemResponse = new CreateCartItemResponse();
            cartItemResponse.setId(cartItem.getId());
            cartItemResponse.setSize(req.getSize());
            cartItemResponse.setNameProduct(cartItem.getProductVariant().getProduct().getName());

            return cartItemResponse;
        }

        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setCart(cart);
        cartItem.setSize(req.getSize());
        cartItem.setQuantity(1);
        cartItem.setProductVariant(existingVariant);
        this.cartItemRepository.save(cartItem);

        CreateCartItemResponse cartItemResponse = new CreateCartItemResponse();
        cartItemResponse.setId(cartItem.getId());
        cartItemResponse.setSize(req.getSize());
        cartItemResponse.setNameProduct(cartItem.getProductVariant().getProduct().getName());

        return cartItemResponse;
    }

    @Override
    public void deleteCartItem(Long id) {
        CartItemEntity cartItem = this.cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        this.cartItemRepository.deleteById(cartItem.getId());
    }
}
