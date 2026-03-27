package com.example.sneaker_store.model.request.permssion;

import com.example.sneaker_store.util.enumEntity.MethodPermission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionRequest {
    @NotBlank(message = "Name không được để trống")
    private String name;

    @NotBlank(message = "Path không được để trống")
    private String path;

    @NotBlank(message = "Entity không được để trống")
    private String entity;

    @NotNull(message = "Method không được để trống")
    private MethodPermission method;

    private String description;
}
