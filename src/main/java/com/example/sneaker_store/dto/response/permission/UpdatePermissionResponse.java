package com.example.sneaker_store.dto.response.permission;

import com.example.sneaker_store.util.enumEntity.MethodPermission;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePermissionResponse {
    private String path;
    private MethodPermission method;
    private String entity;
    private String name;
    private String description;
}