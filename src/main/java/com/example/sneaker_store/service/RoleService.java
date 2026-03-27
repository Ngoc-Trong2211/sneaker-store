package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.role.CreateRoleRequest;
import com.example.sneaker_store.model.request.role.UpdateRoleRequest;
import com.example.sneaker_store.model.response.role.CreateRoleResponse;
import com.example.sneaker_store.model.response.role.UpdateRoleResponse;

public interface RoleService {
    CreateRoleResponse createRole(CreateRoleRequest req);
    UpdateRoleResponse updateRole(UpdateRoleRequest req);
    void updateActiveRole(Long id, boolean active);
}
