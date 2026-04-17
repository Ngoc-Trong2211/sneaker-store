package com.example.sneaker_store.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String newPassword;

    @NotBlank(message = "ConfirmPassword không được để trống")
    private String confirmPassword;
}
