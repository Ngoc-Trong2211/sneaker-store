package com.example.sneaker_store.dto.response.role;

import com.example.sneaker_store.util.enumEntity.MethodPermission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetRoleResponse {
    private DataPage dataPage;
    private List<Role> roles;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataPage{
        private int number;
        private int size;
        private int numberOfElements;
        private int totalPages;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Role{
        private Long id;
        private String name;
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
}
