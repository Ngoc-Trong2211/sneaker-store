package com.example.sneaker_store.dto.request.role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleSpecificationRequest {
    private String name;
    private boolean active;
}
