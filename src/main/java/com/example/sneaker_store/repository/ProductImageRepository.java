package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ProductImageEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
    Optional<List<ProductImageEntity>> findByProductId(String productId);
}
