package com.example.sneaker_store.model.response;

import com.example.sneaker_store.util.enumEntity.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserResponse {
    private String name;
    private String email;
    private String phone;
    private String status;
}
