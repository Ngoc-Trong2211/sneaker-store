package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.model.request.CreateUserRequest;
import com.example.sneaker_store.model.response.CreateUserResponse;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.enumEntity.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CreateUserResponse createUser(CreateUserRequest req) {
        UserEntity user = new UserEntity();
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPhone(req.getPhone());
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        this.userRepository.save(user);
        return this.convertCreateUserResponse(user);
    }

    public CreateUserResponse convertCreateUserResponse(UserEntity user){
        CreateUserResponse res = new CreateUserResponse();
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setPhone(user.getPhone());
        res.setStatus(user.getStatus().toString());
        return res;
    }
}
