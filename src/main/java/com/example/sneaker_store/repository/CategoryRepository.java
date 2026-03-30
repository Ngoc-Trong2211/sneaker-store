package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>, JpaSpecificationExecutor<CategoryEntity> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbl_category WHERE parent_id = :id", nativeQuery = true)
    void deleteCategoryExistsParentId(@Param("id") Long id);
}
