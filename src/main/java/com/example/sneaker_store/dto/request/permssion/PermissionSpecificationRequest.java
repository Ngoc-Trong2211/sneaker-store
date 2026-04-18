package com.example.sneaker_store.dto.request.permssion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionSpecificationRequest {
    private String method;
    private String entity;
}
