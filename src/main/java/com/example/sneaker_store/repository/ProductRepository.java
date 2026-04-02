package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    boolean existsByName(String name);
}
