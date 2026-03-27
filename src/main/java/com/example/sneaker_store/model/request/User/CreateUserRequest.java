package com.example.sneaker_store.model.request.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    @NotBlank(message = "Name must not be empty!")
    private String name;

    @NotBlank(message = "Email must not be empty!")
    private String email;

    @NotBlank(message = "Phone must not be empty!")
    private String phone;

    @NotBlank(message = "Password must not be empty!")
    private String password;

    @NotNull(message = "Role không được để trống!")
    private Long roleId;
}
