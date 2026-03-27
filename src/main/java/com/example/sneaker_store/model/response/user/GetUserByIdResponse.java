package com.example.sneaker_store.model.response.user;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class GetUserByIdResponse {
    private String id;
    private String name;
    private String email;
    private String status;
    private String role;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
}
