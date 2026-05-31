package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.auth.GetAccountResponse;
import com.example.sneaker_store.model.RoleEntity;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.dto.request.auth.LoginRequest;
import com.example.sneaker_store.dto.request.auth.RegisterRequest;
import com.example.sneaker_store.dto.response.auth.LoginResponse;
import com.example.sneaker_store.dto.response.auth.LoginResult;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.AuthService;
import com.example.sneaker_store.service.CartService;
import com.example.sneaker_store.service.RoleService;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.enumEntity.UserStatus;
import com.example.sneaker_store.util.exception.RefreshTokenInvalidException;
import com.example.sneaker_store.util.exception.user.EmailExistsAlreadyException;
import com.example.sneaker_store.util.exception.user.EmailInvalidException;
import com.example.sneaker_store.util.exception.user.PasswordMismatchException;
import com.example.sneaker_store.util.exception.user.StatusInvalidException;
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
import java.util.ArrayList;
import java.util.List;
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
    private final CartService cartService;
    private final RoleService roleService;

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

    private static LoginResponse.UserLogin toUserLogin(UserEntity user) {
        LoginResponse.UserLogin userRes = new LoginResponse.UserLogin();
        userRes.setId(user.getId());
        userRes.setName(user.getName());
        userRes.setEmail(user.getEmail());
        return userRes;
    }

    private static List<String> buildJwtAuthorities(UserEntity user) {
        List<String> authorities = new ArrayList<>();
        if (user.getRole() != null) {
            authorities.add(user.getRole().getName());
            if (user.getRole().getPermissions() != null) {
                user.getRole().getPermissions().forEach(p -> authorities.add(p.getName()));
            }
        }
        return authorities;
    }

    public String createAccessToken(UserEntity user) {
        Instant now = Instant.now();
        Instant validity = now.plus(accessTokenTime, ChronoUnit.SECONDS);
        LoginResponse.UserLogin userPayload = toUserLogin(user);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .claim("sneaker-store", userPayload)
                .claim("authorities", buildJwtAuthorities(user))
                .subject(user.getEmail())
                .expiresAt(validity)
                .issuedAt(now)
                .build();

        JwsHeader header = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String createRefreshToken(String email, LoginResponse.UserLogin user){
        Instant now = Instant.now();
        Instant validity = now.plus(refreshTokenTime, ChronoUnit.SECONDS);

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
    public LoginResult loginUser(LoginRequest req, String guestId) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserEntity user = userService.findByEmail(req.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!user.getStatus().toString().equals("ACTIVE")) {
            throw new StatusInvalidException("Tài khoản đã bị khóa!");
        }
        if (guestId != null && !guestId.isBlank()) {
            cartService.mergeCart(user.getId(), guestId);
        }
        LoginResponse.UserLogin userRes = new LoginResponse.UserLogin();
        userRes.setId(user.getId());
        userRes.setName(user.getName());
        userRes.setEmail(user.getEmail());

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(req.getEmail(), userRes);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserLogin(userRes);
        loginResponse.setAccessToken(accessToken);

        userService.updateRefreshToken(refreshToken, user);

        LoginResult result = new LoginResult();
        result.setLoginResponse(loginResponse);
        result.setRefreshToken(refreshToken);

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

            String accessToken = this.createAccessToken(user);
            loginResponse.setAccessToken(accessToken);

            String refreshToken = this.createRefreshToken(jwt.getSubject(), userRes);
            result.setRefreshToken(refreshToken);
            result.setLoginResponse(loginResponse);

            this.userService.updateRefreshToken(refreshToken, user);
        }

        return result;
    }

    @Override
    public LoginResult registerUser(RegisterRequest req, String guestId) {
        RoleEntity role = roleService.findById(1L);
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
            user.setName(req.getName());
            user.setStatus(UserStatus.ACTIVE);
            if (role!=null) user.setRole(role);
            user = this.userRepository.save(user);
            cartService.mergeCart(user.getId(), guestId);

            LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin();
            userLogin.setId(user.getId());
            userLogin.setEmail(user.getEmail());
            userLogin.setName(user.getName());

            String accessToken = createAccessToken(user);
            String refreshToken = createRefreshToken(user.getEmail(), userLogin);

            userService.updateRefreshToken(refreshToken, user);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(accessToken);
            loginResponse.setUserLogin(userLogin);
            LoginResult result = new LoginResult();
            result.setLoginResponse(loginResponse);
            result.setRefreshToken(refreshToken);

            return result;
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

    @Override
    public GetAccountResponse getAccount() {
        String emailLogin = getCurrentUserLogin().isPresent() ? getCurrentUserLogin().get() : "";
        UserEntity user = this.userService.findByEmail(emailLogin);
        if (user != null){
            GetAccountResponse userLogin = new GetAccountResponse();
            userLogin.setEmail(user.getEmail());
            userLogin.setName(user.getName());
            userLogin.setPhone(user.getPhone());
            userLogin.setEmail(emailLogin);
            RoleEntity role = roleService.findById(user.getRole().getId());
            userLogin.setRole(role.getName());
            userLogin.setStatus(user.getStatus().toString());
            return userLogin;
        }
        else throw new EmailInvalidException("Email is invalid!");
    }

    @Override
    public LoginResult loginWithGoogle(String email, String name, String guestId) {
        RoleEntity role = roleService.findById(1L);
        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setStatus(UserStatus.ACTIVE);
                    if (role != null) newUser.setRole(role);
                    return userRepository.save(newUser);
                });
        if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new StatusInvalidException("Account is locked!");
        }
        if (guestId != null && !guestId.isBlank()) {
            cartService.mergeCart(user.getId(), guestId);
        }
        String accessToken = createAccessToken(user);

        LoginResponse.UserLogin userLogin = new LoginResponse.UserLogin();
        userLogin.setId(user.getId());
        userLogin.setEmail(user.getEmail());
        userLogin.setName(user.getName());
        String refreshToken = createRefreshToken(email, userLogin);
        userService.updateRefreshToken(refreshToken, user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setUserLogin(userLogin);

        LoginResult result = new LoginResult();
        result.setRefreshToken(refreshToken);
        result.setLoginResponse(loginResponse);
        return result;
    }
}
