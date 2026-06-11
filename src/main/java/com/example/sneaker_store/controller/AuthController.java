package com.example.sneaker_store.controller;

import com.example.sneaker_store.dto.request.auth.GetAccountResponse;
import com.example.sneaker_store.dto.request.auth.LoginRequest;
import com.example.sneaker_store.dto.request.auth.RegisterRequest;
import com.example.sneaker_store.dto.response.auth.LoginResponse;
import com.example.sneaker_store.dto.response.auth.LoginResult;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "AUTH-CONTROLLER")
@RequestMapping("/auth/v1")
@CrossOrigin(
        origins = "http://localhost:3000",
        allowCredentials = "true"
)
public class AuthController {
    private final AuthService authService;

    @Value(("${security.authentication.jwt.refresh-token-validity}"))
    private long refreshTokenTime;

    @PostMapping("/auth/login")
    @ApiMessage(message = "Login success")
    @Operation(summary = "Login user", description = "Login user in system")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest auth,
                                               @RequestHeader(value = "X-Guest-Id", required = false) String guestId) {
        log.info("Login");
        LoginResult loginResult = this.authService.loginUser(auth, guestId);

        ResponseCookie responseCookie = ResponseCookie
                .from("refresh", loginResult.getRefreshToken())
                .httpOnly(true)
                .secure(true)
//                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenTime)
                .build();

        ResponseCookie removeGuestCookie = ResponseCookie
                .from("guest_id", "")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.SET_COOKIE, removeGuestCookie.toString())
                .body(loginResult.getLoginResponse());
    }

    @PostMapping("/auth/refresh")
    @ApiMessage(message = "Refresh Token")
    @Operation(summary = "Refresh Token", description = "Refresh Token to continue use website")
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(value = "refresh", defaultValue = "default") String refreshToken){
        LoginResult res = this.authService.refreshToken(refreshToken);
        log.info("refresh");

        ResponseCookie responseCookie = ResponseCookie
                .from("refresh", res.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenTime)
                .secure(true)
//                .secure(false)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res.getLoginResponse());
    }

    @PostMapping("/auth/register")
    @ApiMessage(message = "Register success")
    @Operation(summary = "Register", description = "Register")
    public ResponseEntity<LoginResponse> registerUser(@Valid @RequestBody RegisterRequest auth,
                                                      @RequestHeader(value = "X-Guest-Id", required = false) String guestId) {
        log.info("Đăng ký tạo mới người dùng");
        LoginResult result = this.authService.registerUser(auth, guestId);
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
//                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenTime)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(result.getLoginResponse());
    }

    @PostMapping("/auth/logout")
    @ApiMessage(message = "Logout success")
    public ResponseEntity<Void> logoutUser(@CookieValue(value = "refresh", defaultValue = "default") String refreshToken){
        this.authService.logoutUser(refreshToken);

        ResponseCookie responseCookie = ResponseCookie
                .from("refresh", null)
                .httpOnly(true)
                .secure(true)
//                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(null);
    }

    @GetMapping("/auth/account")
    @ApiMessage(message = "Get account")
    public ResponseEntity<GetAccountResponse> getAccount(){
        log.info("get account");
        return ResponseEntity.ok().body(this.authService.getAccount());
    }
}
