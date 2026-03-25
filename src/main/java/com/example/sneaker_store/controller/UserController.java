package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.CreateUserRequest;
import com.example.sneaker_store.model.response.user.CreateUserResponse;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "USER-CONTROLLER")
@RequestMapping("/user/v1")
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    @ApiMessage(message = "Create new user success")
    @Operation(summary = "Create new user", description = "Create new user in system")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest req) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUser(req));
    }
}
