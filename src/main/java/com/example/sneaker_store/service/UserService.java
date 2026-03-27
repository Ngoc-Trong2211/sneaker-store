package com.example.sneaker_store.service;

import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.model.request.User.ChangePasswordRequest;
import com.example.sneaker_store.model.request.User.CreateUserRequest;
import com.example.sneaker_store.model.request.User.SpecificationUserRequest;
import com.example.sneaker_store.model.request.User.UpdateUserRequest;
import com.example.sneaker_store.model.response.user.CreateUserResponse;
import com.example.sneaker_store.model.response.user.GetUserByIdResponse;
import com.example.sneaker_store.model.response.user.GetUserResponse;
import com.example.sneaker_store.model.response.user.UpdateUserResponse;
import com.example.sneaker_store.util.enumEntity.UserStatus;
import org.springframework.data.domain.Pageable;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest req) throws Exception;
    GetUserResponse getUser(Pageable pageable, SpecificationUserRequest req);
    UpdateUserResponse updateUser(UpdateUserRequest req);
    GetUserResponse.User updateStatus(String id, UserStatus status);
    void disableUser(String id);
    void handleChangePassword(ChangePasswordRequest req);
    UserEntity findByEmail(String email);
    void updateRefreshToken(String refresh, UserEntity user);
    UserEntity findByRefreshTokenAndEmail(String refresh, String email);
    GetUserByIdResponse getUserById(String id);
}
