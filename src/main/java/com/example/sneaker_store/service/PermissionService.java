package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.permssion.CreatePermissionRequest;
import com.example.sneaker_store.model.response.permission.CreatePermissionResponse;

public interface PermissionService {
    CreatePermissionResponse createPermission(CreatePermissionRequest req);
}
