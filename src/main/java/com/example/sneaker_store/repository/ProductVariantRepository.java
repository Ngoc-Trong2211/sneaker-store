package com.example.sneaker_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.sneaker_store.model.ProductVariantEntity;

public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, String>, JpaSpecificationExecutor<ProductVariantEntity> {
    ProductVariantEntity findByColorAndSize(String color, String size);
}
