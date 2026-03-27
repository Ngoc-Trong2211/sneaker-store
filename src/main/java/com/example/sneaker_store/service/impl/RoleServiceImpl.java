package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.PermissionEntity;
import com.example.sneaker_store.model.RoleEntity;
import com.example.sneaker_store.model.request.role.CreateRoleRequest;
import com.example.sneaker_store.model.response.role.CreateRoleResponse;
import com.example.sneaker_store.repository.PermissionRepository;
import com.example.sneaker_store.repository.RoleRepository;
import com.example.sneaker_store.service.RoleService;
import com.example.sneaker_store.util.exception.User.IdInvalidException;
import com.example.sneaker_store.util.exception.role.NameRoleExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j(topic = "ROLE-SERVICE")
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;

    @Override
    public CreateRoleResponse createRole(CreateRoleRequest req) {
        if (this.roleRepository.existsByName(req.getName())) throw new NameRoleExistsException("Tên role đã tồn tại!");
        RoleEntity role = new RoleEntity();
        role.setName(req.getName().toUpperCase());
        role.setDescription(req.getDescription());
        role.setActive(true);

        List<Long> idPermission = req.getPermissionId();
        List<PermissionEntity> permissions = this.permissionRepository.findByIdIn(idPermission);
        if (permissions.isEmpty()) throw new IdInvalidException("Không có permission tồn tại!");
        role.setPermissions(permissions);
        this.roleRepository.save(role);
        return this.modelMapper.map(role, CreateRoleResponse.class);
    }
}
