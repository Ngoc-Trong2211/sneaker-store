package com.example.sneaker_store.dto.request.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    @NotNull(message = "ID người dùng không được để trống")
    private String id;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @Pattern(
        regexp = "^(?:\\+84|0)[35789]\\d{8}$",
        message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @NotNull(message = "Vai trò không được để trống")
    private Long roleId;
}
