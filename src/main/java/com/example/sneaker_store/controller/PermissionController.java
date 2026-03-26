package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.permssion.CreatePermissionRequest;
import com.example.sneaker_store.model.response.permission.CreatePermissionResponse;
import com.example.sneaker_store.service.PermissionService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
