package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    Optional<CartItemEntity> findByCartIdAndProductVariantIdAndSize(String cartId, String variantId, String size);
    List<CartItemEntity> findByCartId(String cartId);
    void deleteAllByCartId(String id);
}
