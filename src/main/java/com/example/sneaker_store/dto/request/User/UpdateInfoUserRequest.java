package com.example.sneaker_store.dto.request.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInfoUserRequest {
    @NotNull(message = "Id must not be empty!")
    private String id;

    @NotBlank(message = "Name must not be empty!")
    private String name;

    @Pattern(
            regexp = "^(?:\\+84|0)[35789]\\d{8}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;
}
