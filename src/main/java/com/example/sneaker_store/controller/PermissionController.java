package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.permssion.CreatePermissionRequest;
import com.example.sneaker_store.model.request.permssion.PermissionSpecificationRequest;
import com.example.sneaker_store.model.request.permssion.UpdatePermissionRequest;
import com.example.sneaker_store.model.response.permission.CreatePermissionResponse;
import com.example.sneaker_store.model.response.permission.GetPermissionResponse;
import com.example.sneaker_store.model.response.permission.UpdatePermissionResponse;
import com.example.sneaker_store.service.PermissionService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "PERMISSION-CONTROLLER")
@RequestMapping("/permission/v1")
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping("/permissions")
    @ApiMessage(message = "Tạo permission thành công")
    @Operation(summary = "Create permission", description = "Tạo mới quyền hạn")
    public ResponseEntity<CreatePermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest req) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(permissionService.createPermission(req));
    }

    @PutMapping("/permissions")
    @ApiMessage(message = "Cập nhật permission thành công")
    @Operation(summary = "Update permission", description = "Cập nhật quyền hạn")
    public ResponseEntity<UpdatePermissionResponse> updatePermission(
            @Valid @RequestBody UpdatePermissionRequest req) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(permissionService.updatePermission(req));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage(message = "Xóa permission thành công")
    @Operation(summary = "Delete permission", description = "Xóa quyền hạn")
    public ResponseEntity<String> deletePermission(@PathVariable("id") Long id) {
        this.permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Xóa permission thành công");
    }

    @GetMapping("/permissions")
    @ApiMessage(message = "Xem danh sách quyền hạn")
    @Operation(summary = "Get permissions", description = "Xem danh sách quyền hạn")
    public ResponseEntity<GetPermissionResponse> getPermission(Pageable pageable,
                                                               PermissionSpecificationRequest req){
        return ResponseEntity.ok(this.permissionService.getPermission(pageable, req));
    }
}
