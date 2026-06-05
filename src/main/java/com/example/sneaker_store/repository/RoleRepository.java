package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> , JpaSpecificationExecutor<RoleEntity> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    @Query("SELECT DISTINCT r FROM RoleEntity r LEFT JOIN FETCH r.permissions WHERE r.name = :name")
    RoleEntity findByName(@Param("name") String name);
}
