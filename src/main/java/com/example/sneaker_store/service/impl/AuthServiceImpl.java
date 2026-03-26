package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.model.request.auth.LoginRequest;
import com.example.sneaker_store.model.request.auth.RegisterRequest;
import com.example.sneaker_store.model.response.auth.LoginResponse;
import com.example.sneaker_store.model.response.auth.LoginResult;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.AuthService;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.enumEntity.UserStatus;
import com.example.sneaker_store.util.exception.RefreshTokenInvalidException;
import com.example.sneaker_store.util.exception.User.EmailExistsAlreadyException;
import com.example.sneaker_store.util.exception.User.EmailInvalidException;
import com.example.sneaker_store.util.exception.User.PasswordMismatchException;
import com.nimbusds.jose.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "AUTH-SERVICE")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${security.authentication.jwt.base64-secret}")
    private String jwtKey;

    @Value("${security.authentication.jwt.access-token-validity}")
    private long accessTokenTime;

    @Value(("${security.authentication.jwt.refresh-token-validity}"))
    private long refreshTokenTime;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    public SecretKey getSecretKey(){
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

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

            String accessToken = this.createAccessToken(req.getEmail(), userRes);
            loginResponse.setAccessToken(accessToken);

            String refreshToken = this.createAccessToken(req.getEmail(), userRes);
            result.setRefreshToken(refreshToken);
            result.setLoginResponse(loginResponse);

            this.userService.updateRefreshToken(refreshToken, user);
        }

        return result;
    }

    public Jwt checkToken(String refresh){
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(this.getSecretKey()).macAlgorithm(JWT_ALGORITHM).build();
        return decoder.decode(refresh);
    }

    @Override
    public LoginResult refreshToken(String refresh) {
        if (refresh.equals("default")) throw new RefreshTokenInvalidException("Token is invalid!");
        LoginResult result = new LoginResult();
        LoginResponse loginResponse = new LoginResponse();
        LoginResponse.UserLogin userRes = new LoginResponse.UserLogin();

        Jwt jwt = this.checkToken(refresh);

        UserEntity user = this.userService.findByRefreshTokenAndEmail(refresh, jwt.getSubject());
        if (user != null){
            userRes.setId(user.getId());
            userRes.setName(user.getName());
            userRes.setEmail(user.getEmail());
            loginResponse.setUserLogin(userRes);

            String accessToken = this.createAccessToken(jwt.getSubject(), userRes);
            loginResponse.setAccessToken(accessToken);

            String refreshToken = this.createAccessToken(jwt.getSubject(), userRes);
            result.setRefreshToken(refreshToken);
            result.setLoginResponse(loginResponse);

            this.userService.updateRefreshToken(refreshToken, user);
        }

        return result;
    }

    @Override
    public void registerUser(RegisterRequest req) {
        if (!validate(req.getEmail())){
            throw new EmailInvalidException("Invalid email format!");
        }
        if (userRepository.existsByEmail(req.getEmail())){
            throw new EmailExistsAlreadyException("Email already exists! Please enter a different email address");
        }
        if (!req.getNewPassword().equals(req.getConfirmPassword())){
            throw new PasswordMismatchException("Password do not match!");
        }
        else{
            UserEntity user = new UserEntity();
            user.setPassword(this.passwordEncoder.encode(req.getNewPassword()));
            user.setEmail(req.getEmail());
            user.setStatus(UserStatus.ACTIVE);
            this.userRepository.save(user);
        }
    }

    public static Optional<String> getCurrentUserLogin(){
        SecurityContext context = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(context.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    @Override
    public void logoutUser(String refresh) {
        if (refresh.equals("default")) throw new RefreshTokenInvalidException("Token is invalid!");
        Jwt jwt = this.checkToken(refresh);
        String emailToken = jwt.getSubject();

        String emailLogin = getCurrentUserLogin().isPresent() ? getCurrentUserLogin().get() : "";
        if (emailLogin.isEmpty() || !emailLogin.equals(emailToken))
            throw new RefreshTokenInvalidException("Email do not match!");
        UserEntity user = this.userService.findByEmail(emailLogin);
        if (user!=null){
            this.userService.updateRefreshToken(null, user);
        }
    }
}
