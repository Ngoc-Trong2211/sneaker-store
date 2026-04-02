package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ProductEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<ProductEntity, String>, JpaSpecificationExecutor<ProductEntity> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, String id);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.id = :categoryId")
    List<ProductEntity> findByCategoryId(Long categoryId);

    @Query("SELECT p FROM ProductEntity p WHERE p.brand.id = :brandId")
    List<ProductEntity> findByBrandId(Long brandId);

    @Query("SELECT p FROM ProductEntity p WHERE p.discount.id = :discountId")
    List<ProductEntity> findByDiscountId(String discountId);
}
