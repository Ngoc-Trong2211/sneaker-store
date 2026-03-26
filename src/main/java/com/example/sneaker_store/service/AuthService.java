package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.auth.LoginRequest;
import com.example.sneaker_store.model.response.auth.LoginResult;

public interface AuthService {
    LoginResult loginUser(LoginRequest req);
}
