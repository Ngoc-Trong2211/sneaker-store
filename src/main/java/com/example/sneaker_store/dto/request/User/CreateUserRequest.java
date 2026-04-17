package com.example.sneaker_store.dto.request.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    @NotBlank(message = "Name must not be empty!")
    private String name;

    @NotBlank(message = "Email must not be empty!")
    private String email;

    @Pattern(
        regexp = "^(?:\\+84|0)[35789]\\d{8}$",
        message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @NotBlank(message = "Password must not be empty!")
    private String password;

    @NotNull(message = "Role không được để trống!")
    private Long roleId;
}
