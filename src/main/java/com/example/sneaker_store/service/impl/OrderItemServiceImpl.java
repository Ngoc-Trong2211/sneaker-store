package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.*;
import com.example.sneaker_store.repository.*;
import com.example.sneaker_store.service.OrderItemService;
import com.example.sneaker_store.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    @PreAuthorize("hasAuthority('ORDER_ITEM_CREATE') or hasAuthority('ORDER_CREATE') or isAnonymous() or hasAuthority('USER')")
    public double addToOrder(String guestId, OrderEntity order) {
        CartEntity cart = getCart(guestId);
        List<CartItemEntity> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống");
        }

        for (CartItemEntity cartItem : cartItems) {
            ProductVariantEntity variant = cartItem.getProductVariant();
            if (variant.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + variant.getProduct().getName() + " đã hết hàng");
            }
        }

        double totalAmount = 0;
        for (CartItemEntity cartItem : cartItems) {
            ProductVariantEntity variant = cartItem.getProductVariant();
            ProductSizeEntity size = this.productSizeRepository.findById(cartItem.getIdSize())
                    .orElseThrow(() -> new RuntimeException("Kích cỡ sản phẩm không tồn tại"));
            if (variant.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm đã hết hàng");
            }
            if (size.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Kích cỡ đã hết hàng");
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
        Instant now = Instant.now();
        DiscountEntity discount = variant.getProduct().getDiscount();
        double price = variant.getProduct().getPrice();
        if (discount != null && discount.getStartTime().isBefore(now) && discount.getEndTime().isAfter(now)) {
            price = price - (price * discount.getPercent()) / 100.0;
            orderItem.setPercent(discount.getPercent());
        }
        orderItem.setPrice(price);
        orderItem.setProductName(variant.getProduct().getName());
        orderItem.setProductId(variant.getProduct().getId());
        orderItem.setSize(cartItem.getSize());
        return orderItem;
    }

    private CartEntity getCart(String guestId) {
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);
        if (email != null && !email.equals("anonymousUser")) {
            UserEntity user = this.userService.findByEmail(email);
            if (user == null) {
                throw new RuntimeException("Không tìm thấy người dùng có email: " + email);
            }
            return cartRepository.findByUserId(user.getId()).orElseThrow(() ->
                            new RuntimeException("Không tìm thấy giỏ hàng của người dùng: " + email));
        }
        if (guestId == null || guestId.isBlank()) {
            throw new RuntimeException("Mã khách vãng lai là bắt buộc");
        }
        return cartRepository.findByGuestId(guestId).orElseThrow(() ->
                        new RuntimeException("Không tìm thấy giỏ hàng của khách: " + guestId));
    }
}
