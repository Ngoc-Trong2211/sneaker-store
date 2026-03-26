package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.User.ChangePasswordRequest;
import com.example.sneaker_store.model.request.User.CreateUserRequest;
import com.example.sneaker_store.model.request.User.SpecificationUserRequest;
import com.example.sneaker_store.model.request.User.UpdateUserRequest;
import com.example.sneaker_store.model.response.user.CreateUserResponse;
import com.example.sneaker_store.model.response.user.GetUserResponse;
import com.example.sneaker_store.model.response.user.UpdateUserResponse;
import com.example.sneaker_store.util.enumEntity.UserStatus;
import org.springframework.data.domain.Pageable;

public interface UserService {
    CreateUserResponse createUser(CreateUserRequest req) throws Exception;
    GetUserResponse getUser(Pageable pageable, SpecificationUserRequest req);
    UpdateUserResponse updateUser(UpdateUserRequest req);
    GetUserResponse.User updateStatus(Long id, UserStatus status);
    void disableUser(Long id);
    void handleChangePassword(ChangePasswordRequest req);
}
