package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.*;
import com.example.sneaker_store.repository.CartItemRepository;
import com.example.sneaker_store.repository.CartRepository;
import com.example.sneaker_store.repository.OrderItemRepository;
import com.example.sneaker_store.service.OrderItemService;
import com.example.sneaker_store.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "ORDER-ITEM-SERVICE")
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final UserService userService;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public double addToOrder(String guestId, OrderEntity order) {
        String email = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
        double totalAmount = 0;
        CartEntity cart;
        if(email != null && !email.equals("anonymousUser")){
            UserEntity user = this.userService.findByEmail(email);
            cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
        }
        else{
            cart = cartRepository.findByGuestId(guestId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
        }
        List<CartItemEntity> listCartItem = cart.getCartItems();
        if (listCartItem.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        for (CartItemEntity cartItem : listCartItem){
            ProductVariantEntity productVariant = cartItem.getProductVariant();
            if (productVariant.getStock() < cartItem.getQuantity()) throw new RuntimeException("Out of stock");

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProductVariant().getProduct().getPrice());
            orderItem.setProductVariant(cartItem.getProductVariant());
            orderItem.setProductName(cartItem.getProductVariant().getProduct().getName());
            orderItem.setOrder(order);
            this.orderItemRepository.save(orderItem);
            totalAmount += (orderItem.getPrice()*orderItem.getQuantity());
        }
        this.cartItemRepository.deleteAll(listCartItem);
        return totalAmount;
    }
}
