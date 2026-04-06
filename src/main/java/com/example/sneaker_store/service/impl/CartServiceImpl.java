package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.CartEntity;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.model.response.cart.CreateCartResponse;
import com.example.sneaker_store.repository.CartRepository;
import com.example.sneaker_store.repository.CategoryRepository;
import com.example.sneaker_store.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sneaker_store.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "CART-SERVICE")
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Override
    public CreateCartResponse createCart(String guestId) {
        String email = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
        if (email != null){
            UserEntity user = this.userService.findByEmail(email);
            CartEntity cart =  this.cartRepository.findByUserId(user.getId())
                    .orElseGet(() -> {
                       CartEntity newCart = new CartEntity();
                       newCart.setUser(user);
                       return this.cartRepository.save(newCart);
                    });
            return this.modelMapper.map(cart, CreateCartResponse.class);
        }
        else {
            if (guestId == null || guestId.isBlank()) {
                throw new RuntimeException("guestId is required for guest");
            }
            CartEntity cart = this.cartRepository.findByGuestId(guestId)
                    .orElseGet(() -> {
                        CartEntity newCart = new CartEntity();
                        newCart.setGuestId(guestId);
                        return this.cartRepository.save(newCart);
                    });
            return this.modelMapper.map(cart, CreateCartResponse.class);
        }
    }
}
