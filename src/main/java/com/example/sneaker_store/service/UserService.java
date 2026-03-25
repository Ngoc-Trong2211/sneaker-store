package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.CreateUserRequest;
import com.example.sneaker_store.model.request.SpecificationUserRequest;
import com.example.sneaker_store.model.response.user.CreateUserResponse;
import com.example.sneaker_store.model.response.user.GetUserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest req) throws Exception;
    GetUserResponse getUser(Pageable pageable, SpecificationUserRequest req);
}
