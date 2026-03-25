package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.CreateUserRequest;
import com.example.sneaker_store.model.response.user.CreateUserResponse;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest req) throws Exception;
}
