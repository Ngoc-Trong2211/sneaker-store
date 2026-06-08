package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    Optional<CartItemEntity> findByCartIdAndProductVariantIdAndIdSize(String cartId, String variantId, Long size);
    List<CartItemEntity> findByCartId(String cartId);
    void deleteAllByCartId(String id);

    @Modifying
    @Transactional
    @Query(value = "DELETE ct FROM tbl_cart_item ct JOIN tbl_cart c ON c.id = ct.cart_id WHERE c.guest_id IS NOT NULL AND c.created_at <= :expiredDate", nativeQuery = true)
    void deleteExpiredGuestCartItems(@Param("expiredDate") Instant expiredDate);
}
