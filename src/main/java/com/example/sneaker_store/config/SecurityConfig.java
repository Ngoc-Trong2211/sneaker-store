package com.example.sneaker_store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

import static org.apache.tomcat.util.http.Method.GET;

@Configuration
@EnableMethodSecurity
@EnableScheduling
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
            AuthenticationEntryPointConfig authenticationEntryPointConfig,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) throws Exception{
        String[] whiteList = {
                "/",
                "/auth/v1/auth/**",
                "/register",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/cart-item/v1/cart-items/**"
        };
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET, "/brand/v1/brands/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/category/v1/categories/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/product/v1/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/review/v1/reviews/product/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/review/v1/reviews/check-eligibility").permitAll()
                        .requestMatchers(HttpMethod.POST, "/order/v1/orders").permitAll()
                        .requestMatchers(HttpMethod.POST, "/review/v1/reviews").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/v1/users/change-password/login").permitAll()
                        .requestMatchers(whiteList).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception ->
                        exception
                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()))
                .oauth2ResourceServer(oauth ->
                        oauth
                                .authenticationEntryPoint(authenticationEntryPointConfig)
                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(manage ->
                        manage.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
