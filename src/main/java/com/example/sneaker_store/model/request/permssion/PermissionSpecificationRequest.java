package com.example.sneaker_store.model.request.permssion;

import lombok.Getter;
import lombok.Setter;
import com.example.sneaker_store.util.enumEntity.MethodPermission;

@Getter
@Setter
public class PermissionSpecificationRequest {
    private MethodPermission method;
    private String entity;
}
