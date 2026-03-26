package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.PermissionEntity;
import com.example.sneaker_store.model.request.permssion.CreatePermissionRequest;
import com.example.sneaker_store.model.response.permission.CreatePermissionResponse;
import com.example.sneaker_store.repository.PermissionRepository;
import com.example.sneaker_store.service.PermissionService;
import com.example.sneaker_store.util.exception.PermissionInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "PERMISSION-SERVICE")
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;

    @Override
    public CreatePermissionResponse createPermission(CreatePermissionRequest req) {
        if (permissionRepository.existsByPathAndMethodAndEntity(req.getPath(), req.getMethod(), req.getEntity())) {
            throw new PermissionInvalidException("Permission đã tồn tại!");
        }

        PermissionEntity permission = new PermissionEntity();
        permission.setPath(req.getPath());
        permission.setEntity(req.getEntity());
        permission.setMethod(req.getMethod());
        permission.setDescription(req.getDescription());

        permissionRepository.save(permission);

        return this.modelMapper.map(permission, CreatePermissionResponse.class);
    }
}
