package com.example.sneaker_store.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAccountResponse {
    private String name;
    private String email;
    private String status;
    private String phone;
    private String role;
}
