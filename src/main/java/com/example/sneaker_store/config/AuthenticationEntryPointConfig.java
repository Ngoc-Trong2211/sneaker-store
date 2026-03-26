package com.example.sneaker_store.config;

import com.example.sneaker_store.model.response.SystemResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class AuthenticationEntryPointConfig implements AuthenticationEntryPoint {
    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public AuthenticationEntryPointConfig(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");

        SystemResponse<Object> res = new SystemResponse<>();
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setData(null);
        String message = Optional.ofNullable(authException.getMessage()).orElse("Unauthorize");
        res.setMessage("Token is invalid =>>>>>>> " + message);

        mapper.writeValue(response.getWriter(), res);
    }
}
