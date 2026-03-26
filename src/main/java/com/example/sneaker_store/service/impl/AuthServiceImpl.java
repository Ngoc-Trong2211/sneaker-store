package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.model.request.auth.LoginRequest;
import com.example.sneaker_store.model.response.auth.LoginResponse;
import com.example.sneaker_store.model.response.auth.LoginResult;
import com.example.sneaker_store.service.AuthService;
import com.example.sneaker_store.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j(topic = "AUTH-SERVICE")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${security.authentication.jwt.base64-secret}")
    private String jwtKey;

    @Value("${security.authentication.jwt.access-token-validity}")
    private long accessTokenTime;

    @Value(("${security.authentication.jwt.refresh-token-validity}"))
    private long refreshTokenTime;

    public String createAccessToken(String email, LoginResponse.UserLogin user){
        Instant now = Instant.now();
        Instant validity = now.plus(accessTokenTime, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .claim("sneaker-store", user)
                .subject(email)
                .expiresAt(validity)
                .issuedAt(now)
                .build();

        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String createRefreshToken(String email, LoginResponse.UserLogin user){
        Instant now = Instant.now();
        Instant validity = now.plus(accessTokenTime, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .claim("sneaker-store", user.getId())
                .subject(email)
                .expiresAt(validity)
                .issuedAt(now)
                .build();

        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    @Override
    public LoginResult loginUser(LoginRequest req) {
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResult result = new LoginResult();
        LoginResponse loginResponse = new LoginResponse();
        LoginResponse.UserLogin userRes = new LoginResponse.UserLogin();

        UserEntity user = this.userService.findByEmail(req.getEmail());
        if (user!=null){
            userRes.setId(user.getId());
            userRes.setName(user.getName());
            userRes.setEmail(user.getEmail());
            loginResponse.setUserLogin(userRes);
        }

        String accessToken = this.createAccessToken(req.getEmail(), userRes);
        loginResponse.setAccessToken(accessToken);

        String refreshToken = this.createAccessToken(req.getEmail(), userRes);
        result.setRefreshToken(refreshToken);
        result.setLoginResponse(loginResponse);

        this.userService.updateRefreshToken(refreshToken, req.getEmail());

        return result;
    }
}
