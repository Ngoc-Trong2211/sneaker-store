package com.example.sneaker_store.config;

import com.example.sneaker_store.dto.response.auth.LoginResult;
import com.example.sneaker_store.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    private String getCookieValue(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("guest_id".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String guestId = getCookieValue(request);
        LoginResult responseBody = authService.loginWithGoogle(email, name, guestId);

        ResponseCookie cookie = ResponseCookie.from("refresh", responseBody.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshTokenTime)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        ResponseCookie deleteGuestCookie = ResponseCookie.from("guest_id", "")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteGuestCookie.toString());

        response.sendRedirect(
                frontendUrl.replaceAll("/$", "") + "/oauth2/redirect?token="
                        + responseBody.getLoginResponse().getAccessToken()
        );
    }
}
