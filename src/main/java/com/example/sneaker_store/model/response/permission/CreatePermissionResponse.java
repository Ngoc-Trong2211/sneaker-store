package com.example.sneaker_store.model.response.permission;

import com.example.sneaker_store.util.enumEntity.MethodPermission;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionResponse {
    private String path;
    private MethodPermission method;
    private String entity;
    private String description;
}
