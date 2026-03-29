package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<BrandEntity, Long> {
}
