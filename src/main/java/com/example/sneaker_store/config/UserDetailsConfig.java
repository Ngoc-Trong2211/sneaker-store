package com.example.sneaker_store.config;

import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.exception.User.EmailInvalidException;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Comment("userDetailsService")
@Service
@RequiredArgsConstructor
public class UserDetailsConfig implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.userService.findByEmail(username);
        if (user == null) throw new EmailInvalidException("Email is invalid!");
        return new User(user.getEmail(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
