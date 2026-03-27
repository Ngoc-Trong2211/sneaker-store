package com.example.sneaker_store.model.response.role;

import com.example.sneaker_store.util.enumEntity.MethodPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateRoleResponse {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private List<Permission> permissions;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Permission{
        private String path;
        private MethodPermission method;
        private String entity;
    }
}
