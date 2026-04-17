package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.permssion.CreatePermissionRequest;
import com.example.sneaker_store.dto.request.permssion.PermissionSpecificationRequest;
import com.example.sneaker_store.dto.request.permssion.UpdatePermissionRequest;
import com.example.sneaker_store.dto.response.permission.CreatePermissionResponse;
import com.example.sneaker_store.dto.response.permission.GetPermissionResponse;
import com.example.sneaker_store.dto.response.permission.UpdatePermissionResponse;
import org.springframework.data.domain.Pageable;

public interface PermissionService {
    CreatePermissionResponse createPermission(CreatePermissionRequest req);
    UpdatePermissionResponse updatePermission(UpdatePermissionRequest req);
    void deletePermission(Long id);
    GetPermissionResponse getPermission(Pageable pageable, PermissionSpecificationRequest req);
}
