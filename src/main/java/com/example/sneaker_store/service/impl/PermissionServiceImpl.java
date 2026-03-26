package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.PermissionEntity;
import com.example.sneaker_store.model.request.permssion.CreatePermissionRequest;
import com.example.sneaker_store.model.request.permssion.UpdatePermissionRequest;
import com.example.sneaker_store.model.response.permission.CreatePermissionResponse;
import com.example.sneaker_store.model.response.permission.UpdatePermissionResponse;
import com.example.sneaker_store.model.request.permssion.PermissionSpecificationRequest;
import com.example.sneaker_store.model.response.permission.GetPermissionResponse;
import com.example.sneaker_store.repository.PermissionRepository;
import com.example.sneaker_store.service.PermissionService;
import com.example.sneaker_store.service.specification.PermissionSpecification;
import com.example.sneaker_store.util.exception.PermissionInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public UpdatePermissionResponse updatePermission(UpdatePermissionRequest req) {
        PermissionEntity permission = permissionRepository.findById(req.getId())
                .orElseThrow(() -> new PermissionInvalidException("Không tìm thấy permission!"));

        if (permissionRepository.existsByPathAndMethodAndEntityAndIdNot(
                req.getPath(),
                req.getMethod(),
                req.getEntity(),
                req.getId()
        )) {
            throw new PermissionInvalidException("Permission đã tồn tại!");
        }

        permission.setPath(req.getPath());
        permission.setEntity(req.getEntity());
        permission.setMethod(req.getMethod());
        permission.setDescription(req.getDescription());

        permissionRepository.save(permission);

        return this.modelMapper.map(permission, UpdatePermissionResponse.class);
    }

    @Override
    public void deletePermission(Long id) {
        PermissionEntity permission = this.permissionRepository.findById(id).orElseThrow(() ->
                new PermissionInvalidException("Quyền hạn này không tồn tại!"));
        this.permissionRepository.deleteById(id);
    }

    @Override
    public GetPermissionResponse getPermission(Pageable pageable, PermissionSpecificationRequest req) {
        Specification<PermissionEntity> spec = PermissionSpecification.specPermission(req);
        Page<PermissionEntity> pagePermission = this.permissionRepository.findAll(spec, pageable);

        GetPermissionResponse res = new GetPermissionResponse();
        GetPermissionResponse.DataPage resPage = this.modelMapper.map(pagePermission, GetPermissionResponse.DataPage.class);
        res.setPage(resPage);

        List<GetPermissionResponse.Permission> permissions = pagePermission.getContent().stream()
                .map(permission -> this.modelMapper.map(permission, GetPermissionResponse.Permission.class)).toList();
        res.setPermissions(permissions);
        return res;
    }
}
