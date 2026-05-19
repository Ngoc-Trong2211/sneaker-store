package com.example.sneaker_store.config;

import com.example.sneaker_store.dto.response.auth.LoginResult;
import com.example.sneaker_store.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseCookie;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthService authService;

    @Value(("${security.authentication.jwt.refresh-token-validity}"))
    private long refreshTokenTime;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        LoginResult responseBody = authService.loginWithGoogle(email, name);

        ResponseCookie cookie = ResponseCookie.from("refresh", responseBody.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshTokenTime)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "accessToken": "%s",
              "userLogin": {
                "id": "%s",
                "email": "%s",
                "name": "%s"
              }
            }
            """.formatted(
                responseBody.getLoginResponse().getAccessToken(),
                responseBody.getLoginResponse().getUserLogin().getId(),
                responseBody.getLoginResponse().getUserLogin().getEmail(),
                responseBody.getLoginResponse().getUserLogin().getName()
        ));

        response.sendRedirect(
                "http://localhost:3000/oauth2/redirect?token="
                        + responseBody.getLoginResponse().getAccessToken()
        );
    }
}
