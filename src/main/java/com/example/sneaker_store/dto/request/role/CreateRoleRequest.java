package com.example.sneaker_store.dto.request.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateRoleRequest {
    @NotBlank(message = "Name không được để trống!")
    private String name;
    @NotBlank(message = "Mô tả không được để trống!")
    private String description;
    @NotNull(message = "Quyền hạn không được để trống!")
    private List<Long> permissionId;
}
