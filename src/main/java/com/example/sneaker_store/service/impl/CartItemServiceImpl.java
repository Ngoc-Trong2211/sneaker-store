package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.cartItem.UpdateQuantityRequest;
import com.example.sneaker_store.dto.response.cartItem.GetCartResponse;
import com.example.sneaker_store.model.CartEntity;
import com.example.sneaker_store.model.CartItemEntity;
import com.example.sneaker_store.model.ProductSizeEntity;
import com.example.sneaker_store.model.ProductVariantEntity;
import com.example.sneaker_store.dto.request.cartItem.CreateCartItemRequest;
import com.example.sneaker_store.dto.response.cartItem.CreateCartItemResponse;
import com.example.sneaker_store.repository.CartItemRepository;
import com.example.sneaker_store.repository.ProductSizeRepository;
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
    private final ProductSizeRepository productSizeRepository;

    @Override
    public CreateCartItemResponse addToCart(CreateCartItemRequest req, String guestId) {
        ProductVariantEntity existingVariant = this.productVariantRepository.findById(req.getVariantId()).orElseThrow(() -> {
            log.warn("Product variant with id: {} not found", req.getVariantId());
            return new RuntimeException("Product variant not found");
        });
        if (existingVariant.getStock() < 1) throw new RuntimeException("San pham khong du");
        CartEntity cart = this.cartService.createCart(guestId);

        ProductSizeEntity size = this.productSizeRepository.findById(req.getIdSize())
                .orElseThrow(() -> new RuntimeException("size item not found"));

        Optional<CartItemEntity> existsCartItem = this.cartItemRepository
                .findByCartIdAndProductVariantIdAndIdSize(cart.getId(), req.getVariantId(), req.getIdSize());

        if (existsCartItem.isPresent()){
            CartItemEntity cartItem = existsCartItem.get();
            cartItem.setIdSize(req.getIdSize());
            cartItem.setSize(size.getSize());
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            this.cartItemRepository.save(cartItem);

            CreateCartItemResponse cartItemResponse = new CreateCartItemResponse();
            cartItemResponse.setId(cartItem.getId());
            cartItem.setIdSize(req.getIdSize());
            cartItemResponse.setSize(size.getSize());
            cartItemResponse.setNameProduct(cartItem.getProductVariant().getProduct().getName());

            return cartItemResponse;
        }

        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setCart(cart);
        cartItem.setIdSize(req.getIdSize());
        cartItem.setSize(size.getSize());
        cartItem.setQuantity(1);
        cartItem.setProductVariant(existingVariant);
        this.cartItemRepository.save(cartItem);

        CreateCartItemResponse cartItemResponse = new CreateCartItemResponse();
        cartItemResponse.setId(cartItem.getId());
        cartItemResponse.setSize(size.getSize());
        cartItemResponse.setNameProduct(cartItem.getProductVariant().getProduct().getName());

        return cartItemResponse;
    }

    @Override
    public void deleteCartItem(Long id) {
        CartItemEntity cartItem = this.cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        this.cartItemRepository.deleteById(cartItem.getId());
    }

    @Override
    public int updateQuantity(UpdateQuantityRequest req) {
        CartItemEntity cartItem = this.cartItemRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (req.getAction()!=null && req.getAction().equalsIgnoreCase("increase")){
            ProductVariantEntity variant = cartItem.getProductVariant();

            cartItem.setQuantity(cartItem.getQuantity() + req.getQuantity());
        }
        if (req.getAction()!=null && req.getAction().equalsIgnoreCase("decrease")){
            cartItem.setQuantity(cartItem.getQuantity() - req.getQuantity());
        }
        return 0;
    }
}
