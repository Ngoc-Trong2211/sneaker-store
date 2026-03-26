package com.example.sneaker_store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationEntryPointConfig authenticationEntryPointConfig) throws Exception{
        String[] whiteList = {
                "/",
                "/api/v1/auth/**",
                "/register",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
        };
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(whiteList).permitAll()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth ->
                        oauth
                                .authenticationEntryPoint(authenticationEntryPointConfig)
                                .jwt(Customizer.withDefaults()))
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(manage ->
                        manage.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
