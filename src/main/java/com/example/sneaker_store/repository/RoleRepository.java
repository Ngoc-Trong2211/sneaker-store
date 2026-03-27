package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    boolean existsByName(String name);
}
