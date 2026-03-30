package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>, JpaSpecificationExecutor<CategoryEntity> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
