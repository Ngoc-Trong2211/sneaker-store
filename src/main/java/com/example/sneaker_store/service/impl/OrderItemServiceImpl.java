package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.*;
import com.example.sneaker_store.repository.*;
import com.example.sneaker_store.service.OrderItemService;
import com.example.sneaker_store.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
    private final ProductSizeRepository productSizeRepository;

    @Transactional
    @Override
    public double addToOrder(String guestId, OrderEntity order) {
        CartEntity cart = getCart(guestId);
        List<CartItemEntity> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for (CartItemEntity cartItem : cartItems) {
            ProductVariantEntity variant = cartItem.getProductVariant();
            if (variant.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException(variant.getProduct().getName() + " out of stock");
            }
        }

        double totalAmount = 0;
        for (CartItemEntity cartItem : cartItems) {
            ProductVariantEntity variant = cartItem.getProductVariant();
            ProductSizeEntity size = this.productSizeRepository.findById(cartItem.getIdSize())
                    .orElseThrow(() -> new RuntimeException("size item not found"));
            if (variant.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Out of stock");
            }
            if (size.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Size out of stock");
            }
            size.setQuantity(size.getQuantity() - cartItem.getQuantity());
            variant.setStock(variant.getStock() - cartItem.getQuantity());
            ProductEntity product = variant.getProduct();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            OrderItemEntity orderItem = getOrderItemEntity(order, cartItem);
            totalAmount += orderItem.getPrice() * orderItem.getQuantity();
            orderItemRepository.save(orderItem);
        }
        cartItemRepository.deleteAllByCartId(cart.getId());
        return totalAmount;
    }

    private static @NonNull OrderItemEntity getOrderItemEntity(OrderEntity order, CartItemEntity cartItem) {
        ProductVariantEntity variant = cartItem.getProductVariant();
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrder(order);
        orderItem.setProductVariant(variant);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(variant.getProduct().getPrice());
        orderItem.setProductName(variant.getProduct().getName());
        orderItem.setSize(cartItem.getSize());
        return orderItem;
    }

    private CartEntity getCart(String guestId) {
        String email = AuthServiceImpl.getCurrentUserLogin().isPresent()
                ? AuthServiceImpl.getCurrentUserLogin().get() : null;
        if (email != null && !email.equals("anonymousUser")) {
            UserEntity user = this.userService.findByEmail(email);
            return cartRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Cart not found"));
        }
        else return cartRepository.findByGuestId(guestId).orElseThrow(() -> new RuntimeException("Cart not found"));
    }
}
