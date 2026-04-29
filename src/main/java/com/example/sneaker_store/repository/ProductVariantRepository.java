package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.sneaker_store.model.ProductVariantEntity;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, String>, JpaSpecificationExecutor<ProductVariantEntity> {
    ProductVariantEntity findByColorAndSizeAndProductId(String color, String size, String productId);
    Optional<List<ProductVariantEntity>> findByProductId(String productId);

}
