package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<ProductEntity, String>, JpaSpecificationExecutor<ProductEntity> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, String id);
}
