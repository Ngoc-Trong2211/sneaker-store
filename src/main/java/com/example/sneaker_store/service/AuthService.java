package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.auth.LoginRequest;
import com.example.sneaker_store.model.request.auth.RegisterRequest;
import com.example.sneaker_store.model.response.auth.LoginResponse;
import com.example.sneaker_store.model.response.auth.LoginResult;

public interface AuthService {
    LoginResult loginUser(LoginRequest req, String guestId);
    LoginResult refreshToken(String refresh);
    void registerUser(RegisterRequest req);
    void logoutUser(String refresh);
    LoginResponse.UserLogin getAccount();
}
