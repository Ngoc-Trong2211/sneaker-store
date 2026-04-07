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
import java.util.Optional;

@Service
@Slf4j(topic = "CART-SERVICE")
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserService userService;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartEntity createCart(String guestId) {
        String email = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
        if (email != null && !email.equals("anonymousUser")){
            UserEntity user = this.userService.findByEmail(email);
            CartEntity cart = this.cartRepository.findByUserId(user.getId())
                    .orElseGet(() -> {
                       CartEntity newCart = new CartEntity();
                       newCart.setUserId(user.getId());
                       return this.cartRepository.save(newCart);
                    });
            if (guestId != null && !guestId.isBlank()) {
                mergeCart(cart, guestId);
            }
            return cart;
        }
        else {
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
    }

    @Transactional
    public void mergeCart(CartEntity cartUser, String guestId){
        Optional<CartEntity> cartGuest = this.cartRepository.findByGuestId(guestId);
        if (cartGuest.isPresent()) {
            List<CartItemEntity> listCartItem = this.cartItemRepository.findByCartId(cartGuest.get().getId());
            for (CartItemEntity cartItem : listCartItem){
                 Optional<CartItemEntity> existsCartItem = this.cartItemRepository
                         .findByCartIdAndProductVariantId(cartUser.getId(), cartItem.getProductVariant().getId());
                 if (existsCartItem.isPresent()){
                     CartItemEntity userCartItem = existsCartItem.get();
                     userCartItem.setQuantity(userCartItem.getQuantity() + cartItem.getQuantity());
                     this.cartItemRepository.save(userCartItem);
                 }
                 else{
                     CartItemEntity newCartItem = new CartItemEntity();
                     newCartItem.setCart(cartUser);
                     newCartItem.setProductVariant(cartItem.getProductVariant());
                     newCartItem.setQuantity(cartItem.getQuantity());
                     this.cartItemRepository.save(newCartItem);
                 }
            }
            this.cartItemRepository.deleteAll(listCartItem);
            this.cartRepository.delete(cartGuest.get());
        }
    }
}
