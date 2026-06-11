package com.example.sneaker_store.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetAccountResponse {
    private String name;
    private String email;
    private String status;
    private String phone;
    private String role;
    private List<String> permissions;
}
