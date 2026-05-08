package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ProductEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<ProductEntity, String>, JpaSpecificationExecutor<ProductEntity> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, String id);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.id = :categoryId")
    List<ProductEntity> findByCategoryId(Long categoryId);

    @Query("SELECT p FROM ProductEntity p WHERE p.brand.id = :brandId")
    List<ProductEntity> findByBrandId(Long brandId);

    @Query("SELECT p FROM ProductEntity p WHERE p.discount.id = :discountId")
    List<ProductEntity> findByDiscountId(String discountId);

    Optional<ProductEntity> findByName(String name);

    Optional<ProductEntity> findBySlug(String slug);

    @Transactional
    @Modifying
    @Query("UPDATE ProductEntity p SET p.discount = null WHERE p.discount.id = :discountId")
    void clearDiscountFromProducts(String discountId);

    @Transactional
    @Modifying
    @Query("""
        UPDATE ProductEntity p
            SET p.discount = null
                WHERE p.discount.id in (
                    SELECT d.id
                    FROM DiscountEntity d
                    WHERE d.status = 'EXPIRED'
                )
    """)
    void autoClearDiscountFromProducts();
}
