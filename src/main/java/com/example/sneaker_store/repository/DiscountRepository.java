package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<DiscountEntity, String> {
}
