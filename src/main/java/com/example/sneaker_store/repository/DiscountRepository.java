package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.DiscountEntity;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DiscountRepository extends JpaRepository<DiscountEntity, String> {
    boolean existsByNameApply(String nameApply);

    @Modifying
    @Transactional
    @Query("UPDATE DiscountEntity d SET d.status = 'EXPIRED' WHERE d.endTime < CURRENT_TIMESTAMP")
    void updateExpiredDiscounts();
}
