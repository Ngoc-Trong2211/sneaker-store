package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ProductImageEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.sneaker_store.model.ProductVariantEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariantEntity, String>, JpaSpecificationExecutor<ProductVariantEntity> {
    ProductVariantEntity findByColorAndProductId(String color, String productId);
    Optional<List<ProductVariantEntity>> findByProductId(String productId);
    Optional<ProductVariantEntity> findBySku(String sku);

    @Modifying
    @Transactional
    @Query("UPDATE ProductVariantEntity p SET p.status = 'DELETED' WHERE p.product.id = :id")
    void deleteSoftProductVariant(String id);
}
