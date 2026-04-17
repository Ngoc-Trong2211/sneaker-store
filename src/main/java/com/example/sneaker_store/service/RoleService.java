package com.example.sneaker_store.service;

import com.example.sneaker_store.model.RoleEntity;
import com.example.sneaker_store.dto.request.role.CreateRoleRequest;
import com.example.sneaker_store.dto.request.role.RoleSpecificationRequest;
import com.example.sneaker_store.dto.request.role.UpdateRoleRequest;
import com.example.sneaker_store.dto.response.role.CreateRoleResponse;
import com.example.sneaker_store.dto.response.role.GetRoleResponse;
import com.example.sneaker_store.dto.response.role.UpdateRoleResponse;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleEntity findById(Long id);
    CreateRoleResponse createRole(CreateRoleRequest req);
    UpdateRoleResponse updateRole(UpdateRoleRequest req);
    void updateActiveRole(Long id, boolean active);
    GetRoleResponse handleGetRole(Pageable pageable, RoleSpecificationRequest req);
}
