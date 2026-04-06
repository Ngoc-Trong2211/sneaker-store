package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
}
