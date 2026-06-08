package com.example.sneaker_store.config;

import com.example.sneaker_store.dto.response.auth.LoginResult;
import com.example.sneaker_store.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseCookie;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final AuthService authService;

    @Value(("${security.authentication.jwt.refresh-token-validity}"))
    private long refreshTokenTime;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    private String firstHeaderValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.split(",")[0].trim();
    }

    private boolean isLocalhost(String url) {
        return url != null && (url.contains("localhost") || url.contains("127.0.0.1"));
    }

    private String normalizeBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        return url.replaceAll("/$", "");
    }

    private String getRequestOrigin(HttpServletRequest request) {
        String host = firstHeaderValue(request.getHeader("X-Forwarded-Host"));
        if (host == null) {
            host = firstHeaderValue(request.getHeader(HttpHeaders.HOST));
        }
        if (host == null) {
            return null;
        }

        String proto = firstHeaderValue(request.getHeader("X-Forwarded-Proto"));
        if (proto == null) {
            proto = request.getScheme();
        }
        return proto + "://" + host;
    }

    private String getFrontendBaseUrl(HttpServletRequest request) {
        String configuredFrontendUrl = normalizeBaseUrl(frontendUrl);
        String requestOrigin = normalizeBaseUrl(getRequestOrigin(request));

        if (requestOrigin != null && !isLocalhost(requestOrigin) && isLocalhost(configuredFrontendUrl)) {
            return requestOrigin;
        }
        return configuredFrontendUrl != null ? configuredFrontendUrl : "http://localhost:3000";
    }

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
                .secure(true)
                .path("/")
                .maxAge(refreshTokenTime)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        ResponseCookie deleteGuestCookie = ResponseCookie.from("guest_id", "")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteGuestCookie.toString());

        String redirectUrl = UriComponentsBuilder
                .fromHttpUrl(getFrontendBaseUrl(request))
                .path("/oauth2/redirect")
                .queryParam("token", responseBody.getLoginResponse().getAccessToken())
                .build()
                .toUriString();
        log.info("OAuth2 login succeeded for {}, redirecting to {}", email, redirectUrl.split("\\?")[0]);
        response.sendRedirect(redirectUrl);
    }
}
