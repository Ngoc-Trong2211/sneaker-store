package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.CartItemEntity;
import com.example.sneaker_store.model.ProductVariantEntity;
import com.example.sneaker_store.model.request.cartItem.CreateCartItemRequest;
import com.example.sneaker_store.model.response.cartItem.CreateCartItemResponse;
import com.example.sneaker_store.repository.CartItemRepository;
import com.example.sneaker_store.repository.ProductVariantRepository;
import com.example.sneaker_store.service.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "CART-ITEM-SERVICE")
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    public CreateCartItemResponse createCartItem(CreateCartItemRequest req) {
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(req.getVariantId()).orElseThrow(() -> {
            log.warn("Product variant with id: {} not found", req.getVariantId());
            return new RuntimeException("Product variant not found");
        });
        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setQuantity(req.getQuantity());
        cartItem.setProductVariant(existingVariant);
        this.cartItemRepository.save(cartItem);

        CreateCartItemResponse cartItemResponse = new CreateCartItemResponse();
        cartItemResponse.setId(cartItem.getId());
        cartItemResponse.setSize(cartItem.getProductVariant().getSize());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        cartItemResponse.setColor(cartItem.getProductVariant().getColor());
        cartItemResponse.setNameProduct(cartItem.getProductVariant().getProduct().getName());

        return cartItemResponse;
    }
}
