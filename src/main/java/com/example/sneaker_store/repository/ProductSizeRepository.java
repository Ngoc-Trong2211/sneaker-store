package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ProductSizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductSizeRepository extends JpaRepository<ProductSizeEntity, Long> {
    Optional<List<ProductSizeEntity>> findByVariantId(String id);
    ProductSizeEntity findByVariantIdAndSizeAndIdNot(String variantId, String size, Long id);
}
