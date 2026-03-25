package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.model.request.CreateUserRequest;
import com.example.sneaker_store.model.response.CreateUserResponse;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.enumEntity.UserStatus;
import com.example.sneaker_store.util.exception.User.EmailExistsAlreadyException;
import com.example.sneaker_store.util.exception.User.EmailInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest req) throws Exception {
        UserEntity user = new UserEntity();
        if(!validate(req.getEmail())){
            throw new EmailInvalidException("Invalid email format!");
        }
        if(this.userRepository.existsByEmail(req.getEmail())){
            throw new EmailExistsAlreadyException("Email is exists");
        }
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPhone(req.getPhone());
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        this.userRepository.save(user);
        return this.modelMapper.map(user, CreateUserResponse.class);
    }
}
