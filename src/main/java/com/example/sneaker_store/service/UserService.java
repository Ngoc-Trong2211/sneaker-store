package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.User.*;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.dto.response.user.CreateUserResponse;
import com.example.sneaker_store.dto.response.user.GetUserByIdResponse;
import com.example.sneaker_store.dto.response.user.GetUserResponse;
import com.example.sneaker_store.dto.response.user.UpdateUserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest req) throws Exception;
    GetUserResponse getUser(Pageable pageable, SpecificationUserRequest req);
    UpdateUserResponse updateUser(UpdateUserRequest req);
    UpdateUserResponse updateUserInfo(UpdateInfoUserRequest req);
    GetUserResponse.User updateStatus(String id, String status);
    void disableUser(String id);
    void handleChangePassword(ChangePasswordRequest req);
    UserEntity findByEmail(String email);
    void updateRefreshToken(String refresh, UserEntity user);
    UserEntity findByRefreshTokenAndEmail(String refresh, String email);
    GetUserByIdResponse getUserById(String id);
}
