package com.example.sneaker_store.model.request.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    @NotNull(message = "Id must not be empty!")
    private Long id;

    @NotBlank(message = "Name must not be empty!")
    private String name;

    @NotBlank(message = "Phone must not be empty!")
    private String phone;
}
