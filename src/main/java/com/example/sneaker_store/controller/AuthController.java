package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.auth.LoginRequest;
import com.example.sneaker_store.model.response.auth.LoginResponse;
import com.example.sneaker_store.model.response.auth.LoginResult;
import com.example.sneaker_store.service.AuthService;
import com.example.sneaker_store.util.ApiMessage;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-CONTROLLER")
@RequestMapping("/auth/v1")
public class AuthController {
    private final AuthService authService;

    @Value(("${security.authentication.jwt.refresh-token-validity}"))
    private long refreshTokenTime;

    @PostMapping("/auth/login")
    @ApiMessage(message = "Login success")
    @Operation(summary = "Login user", description = "Login user in system")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest auth) {
        log.info("Login");
        LoginResult loginResult = this.authService.loginUser(auth);

        ResponseCookie responseCookie = ResponseCookie
                .from("refresh", loginResult.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenTime)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(loginResult.getLoginResponse());
    }
}
