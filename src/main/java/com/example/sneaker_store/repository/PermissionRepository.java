package com.example.sneaker_store.repository;

import com.example.sneaker_store.model.PermissionEntity;
import com.example.sneaker_store.util.enumEntity.MethodPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long>, JpaSpecificationExecutor<PermissionEntity> {
    boolean existsByNameAndPathAndMethodAndEntity(String name, String path, MethodPermission method, String entity);
    boolean existsByNameAndPathAndMethodAndEntityAndIdNot(String name, String path, MethodPermission method, String entity, Long id);

    @Query(value = "SELECT * FROM tbl_permission WHERE id in :permissionId", nativeQuery = true)
    List<PermissionEntity> findByIdIn(@Param("permissionId") List<Long> permissionId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tbl_role_permission WHERE permission_id = :permissionId", nativeQuery = true)
    void deleteRelationshipPermissionId(@Param("permissionId") Long permissionId);

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
