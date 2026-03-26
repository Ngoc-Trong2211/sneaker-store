package com.example.sneaker_store.controller;

import com.example.sneaker_store.model.request.auth.LoginRequest;
import com.example.sneaker_store.model.request.auth.RegisterRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/auth/refresh")
    @ApiMessage(message = "Refresh Token")
    @Operation(summary = "Refresh Token", description = "Refresh Token to continue use website")
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(value = "refresh", defaultValue = "default") String refreshToken){
        LoginResult res = this.authService.refreshToken(refreshToken);

        ResponseCookie responseCookie = ResponseCookie
                .from("refresh", res.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenTime)
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res.getLoginResponse());
    }

    @PostMapping("/auth/register")
    @ApiMessage(message = "Register success")
    @Operation(summary = "Register", description = "Register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest auth) {
        log.info("Đăng ký tạo mới người dùng");
        this.authService.registerUser(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body("Đăng ký thành công!");
    }

    @PostMapping("/auth/logout")
    @ApiMessage(message = "Logout success")
    public ResponseEntity<Void> logoutUser(@CookieValue(value = "refresh", defaultValue = "default") String refreshToken){
        this.authService.logoutUser(refreshToken);

        ResponseCookie responseCookie = ResponseCookie
                .from("refresh", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(null);
    }

    @GetMapping("/auth/account")
    @ApiMessage(message = "Get account")
    public ResponseEntity<LoginResponse.UserLogin> getAccount(){
        return ResponseEntity.ok().body(this.authService.getAccount());
    }
}
