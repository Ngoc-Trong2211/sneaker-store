package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.PermissionEntity;
import com.example.sneaker_store.model.RoleEntity;
import com.example.sneaker_store.model.request.role.CreateRoleRequest;
import com.example.sneaker_store.model.request.role.UpdateRoleRequest;
import com.example.sneaker_store.model.response.role.CreateRoleResponse;
import com.example.sneaker_store.model.response.role.UpdateRoleResponse;
import com.example.sneaker_store.repository.PermissionRepository;
import com.example.sneaker_store.repository.RoleRepository;
import com.example.sneaker_store.service.RoleService;
import com.example.sneaker_store.util.exception.User.IdInvalidException;
import com.example.sneaker_store.util.exception.role.NameRoleExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "ROLE-SERVICE")
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;

    public RoleEntity findById(Long id) {
        return this.roleRepository.findById(id).isPresent() ? this.roleRepository.findById(id).get() : null;
    }

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

    @Override
    public UpdateRoleResponse updateRole(UpdateRoleRequest req) {
        RoleEntity role = this.findById(req.getId());
        if (role == null) throw new IdInvalidException("Role không tồn tại!");

        // Kiểm tra tên role đã có ở các id khác chưa
        if (this.roleRepository.existsByNameAndIdNot(req.getName(), req.getId()))
            throw new NameRoleExistsException("Tên role đã tồn tại!");
        role.setName(req.getName().toUpperCase());
        role.setDescription(req.getDescription());

        Set<Long> currentIdPermissions = role.getPermissions().stream().
                map(PermissionEntity::getId).collect(Collectors.toSet());
        Set<Long> idPermissions = new HashSet<>(req.getPermissionId());

        if (!currentIdPermissions.equals(idPermissions)){
            List<PermissionEntity> permissions = this.permissionRepository.findByIdIn(idPermissions.stream().toList());
            if (permissions.isEmpty()) throw new IdInvalidException("Không có permission tồn tại!");
            role.setPermissions(permissions);
            this.roleRepository.save(role);
        }

        this.roleRepository.save(role);

        return this.modelMapper.map(role, UpdateRoleResponse.class);
    }
}
