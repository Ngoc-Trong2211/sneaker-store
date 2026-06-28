package com.example.sneaker_store.dto.request.permssion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePermissionRequest {
    @NotBlank(message = "Tên quyền hạn không được để trống")
    private String name;

    @NotBlank(message = "Đường dẫn không được để trống")
    private String path;

    @NotBlank(message = "Đối tượng không được để trống")
    private String entity;

    @NotNull(message = "Phương thức không được để trống")
    private String method;

    private String description;
}
