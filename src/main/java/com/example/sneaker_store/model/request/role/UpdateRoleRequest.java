package com.example.sneaker_store.model.request.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateRoleRequest {
    @NotNull(message = "Id không được null")
    private Long id;

    @NotBlank(message = "Tên role không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Quyền hạn không được để trống!")
    private List<Long> permissionId;
}