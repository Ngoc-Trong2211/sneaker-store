package com.example.sneaker_store.dto.response.permission;

import com.example.sneaker_store.util.enumEntity.MethodPermission;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionResponse {
    private String path;
    private String name;
    private MethodPermission method;
    private String entity;
    private String description;
}
