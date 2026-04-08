package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.role.CreateRoleRequest;
import com.example.sneaker_store.model.request.role.RoleSpecificationRequest;
import com.example.sneaker_store.model.request.role.UpdateRoleRequest;
import com.example.sneaker_store.model.response.role.CreateRoleResponse;
import com.example.sneaker_store.model.response.role.GetRoleResponse;
import com.example.sneaker_store.model.response.role.UpdateRoleResponse;
import com.example.sneaker_store.service.RoleService;
import com.example.sneaker_store.util.ApiMessage;
import com.example.sneaker_store.util.exception.user.IdInvalidException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "ROLE-CONTROLLER")
@RequestMapping("/role/v1")
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/roles")
    @ApiMessage(message = "Create role")
    @Operation(summary = "Create role", description = "Tạo mới vai trò người dùng")
    public ResponseEntity<CreateRoleResponse> createRole(@Valid @RequestBody CreateRoleRequest req)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createRole(req));
    }

    @PutMapping("/roles")
    @ApiMessage(message = "Update role")
    @Operation(summary = "Update role", description = "Cập nhật vai trò người dùng")
    public ResponseEntity<UpdateRoleResponse> updateRole(@Valid @RequestBody UpdateRoleRequest req)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(this.roleService.updateRole(req));
    }

    @PatchMapping("/roles/{id}")
    @ApiMessage(message = "Update role active")
    @Operation(
            summary = "Update role active",
            description = "Bật / tắt role theo id"
    )
    public ResponseEntity<String> patchRoleActive(@PathVariable Long id, @RequestParam Boolean active)
            throws IdInvalidException {
        this.roleService.updateActiveRole(id, active);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Cập nhật thành công!");
    }

    @GetMapping("/roles")
    @Operation(summary = "Get role", description = "Lấy danh sách vai trò trong hệ thống")
    @ApiMessage(message = "Get role")
    public ResponseEntity<GetRoleResponse> getUser(RoleSpecificationRequest req, @ParameterObject Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.handleGetRole(pageable, req));
    }
}
