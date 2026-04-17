package com.example.sneaker_store.service;

import com.example.sneaker_store.model.RoleEntity;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.dto.request.User.CreateUserRequest;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private UserRepository userRepository;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleService = mock(RoleService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        modelMapper = new ModelMapper();

        userService = new UserServiceImpl(
                userRepository,
                passwordEncoder,
                modelMapper,
                roleService
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createUser_success() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("test@gmail.com");
        req.setName("Trong");
        req.setPhone("0123456789");
        req.setPassword("123456");
        req.setRoleId(1L);

        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setActive(true);

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(req.getPhone())).thenReturn(false);
        when(roleService.findById(1L)).thenReturn(role);
        when(passwordEncoder.encode(any())).thenReturn("encoded-password");

        var result = userService.createUser(req);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void getUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void updateStatus() {
    }

    @Test
    void disableUser() {
    }

    @Test
    void handleChangePassword() {
    }

    @Test
    void findByEmail() {
    }

    @Test
    void updateRefreshToken() {
    }

    @Test
    void findByRefreshTokenAndEmail() {
    }

    @Test
    void getUserById() {
    }
}