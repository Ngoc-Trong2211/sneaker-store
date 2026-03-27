package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.role.CreateRoleRequest;
import com.example.sneaker_store.model.response.role.CreateRoleResponse;

public interface RoleService {
    CreateRoleResponse createRole(CreateRoleRequest req);
}
