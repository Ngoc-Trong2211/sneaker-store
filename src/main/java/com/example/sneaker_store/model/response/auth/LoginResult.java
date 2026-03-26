package com.example.sneaker_store.model.response.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResult {
    private String refreshToken;
    private LoginResponse loginResponse;
}
