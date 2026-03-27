package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> , JpaSpecificationExecutor<RoleEntity> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    @Query(value = "SELECT * FROM tbl_role WHERE name = :name", nativeQuery = true)
    RoleEntity findByName(@Param("name") String name);
}
