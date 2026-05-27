package com.example.sneaker_store.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserByIdResponse {
    private String name;
    private String email;
    private String status;
    private String phone;
    private String role;
}
