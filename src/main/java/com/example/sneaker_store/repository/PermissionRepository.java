package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.PermissionEntity;
import com.example.sneaker_store.util.enumEntity.MethodPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long>, JpaSpecificationExecutor<PermissionEntity> {
    boolean existsByPathAndMethodAndEntity(String path, MethodPermission method, String entity);
    boolean existsByPathAndMethodAndEntityAndIdNot(String path, MethodPermission method, String entity, Long id);

    @Query(value = "SELECT * FROM tbl_permission WHERE id in :permissionId", nativeQuery = true)
    List<PermissionEntity> findByIdIn(@Param("permissionId") List<Long> permissionId);
}
