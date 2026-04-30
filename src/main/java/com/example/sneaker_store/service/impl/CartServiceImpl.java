package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.CartEntity;
import com.example.sneaker_store.model.CartItemEntity;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.repository.CartItemRepository;
import com.example.sneaker_store.repository.CartRepository;
import com.example.sneaker_store.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.example.sneaker_store.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "CART-SERVICE")
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserService userService;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartEntity createCart(String guestId) {
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);
        if (email != null && !email.equals("anonymousUser")) {
            UserEntity user = this.userService.findByEmail(email);

            return this.cartRepository.findByUserId(user.getId())
                    .orElseGet(() -> {
                        CartEntity newCart = new CartEntity();
                        newCart.setUserId(user.getId());
                        return this.cartRepository.save(newCart);
                    });
        }
        if (guestId == null || guestId.isBlank()) {
            throw new RuntimeException("guestId is required for guest");
        }
        return this.cartRepository.findByGuestId(guestId)
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setGuestId(guestId);
                    return this.cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional
    public void mergeCart(String userId, String guestId) {

        if (guestId == null || guestId.isBlank()) return;

        CartEntity guestCart = this.cartRepository.findByGuestId(guestId).orElse(null);
        if (guestCart == null) return;

        CartEntity userCart = this.cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUserId(userId);
                    return this.cartRepository.save(newCart);
                });
        List<CartItemEntity> guestItems = this.cartItemRepository.findByCartId(guestCart.getId());
        List<CartItemEntity> userItems = this.cartItemRepository.findByCartId(userCart.getId());
        Map<String, CartItemEntity> userItemMap = userItems.stream()
                .collect(Collectors.toMap(
                        i -> i.getProductVariant().getId(),
                        i -> i
                ));
        for (CartItemEntity guestItem : guestItems) {
            String variantId = guestItem.getProductVariant().getId();

            if (userItemMap.containsKey(variantId)) {
                CartItemEntity userItem = userItemMap.get(variantId);
                userItem.setQuantity(userItem.getQuantity() + guestItem.getQuantity());
            } else {
                CartItemEntity newItem = new CartItemEntity();
                newItem.setCart(userCart);
                newItem.setProductVariant(guestItem.getProductVariant());
                newItem.setQuantity(guestItem.getQuantity());
                this.cartItemRepository.save(newItem);
            }
        }
        this.cartItemRepository.deleteAllByCartId(guestCart.getId());
        this.cartRepository.deleteById(guestCart.getId());
    }
}
