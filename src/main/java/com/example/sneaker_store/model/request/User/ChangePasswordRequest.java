package com.example.sneaker_store.model.request.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank(message = "Email must not be empty!")
    private String email;

    @NotBlank(message = "CurrentPassword must not be empty!")
    private String currentPassword;

    @NotBlank(message = "Password must not be empty!")
    private String newPassword;

    @NotBlank(message = "ConfirmPassword must not be empty!")
    private String confirmPassword;
}
