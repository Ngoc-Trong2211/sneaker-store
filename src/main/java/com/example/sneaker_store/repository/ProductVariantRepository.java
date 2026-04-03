package com.example.sneaker_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sneaker_store.model.ProductVariantEntity;

public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, String> {
    ProductVariantEntity findByColorAndSize(String color, String size);
}
