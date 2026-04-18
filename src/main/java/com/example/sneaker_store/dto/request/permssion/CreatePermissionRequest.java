package com.example.sneaker_store.dto.request.permssion;

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
    private String method;

    private String description;
}
