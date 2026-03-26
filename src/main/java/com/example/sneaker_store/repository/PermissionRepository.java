package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.PermissionEntity;
import com.example.sneaker_store.util.enumEntity.MethodPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    boolean existsByPathAndMethodAndEntity(String path, MethodPermission method, String entity);
    boolean existsByPathAndMethodAndEntityAndIdNot(String path, MethodPermission method, String entity, Long id);
}
