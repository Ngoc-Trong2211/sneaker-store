package com.example.sneaker_store.dto.response.permission;

import com.example.sneaker_store.util.enumEntity.MethodPermission;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class GetPermissionResponse {
    private DataPage page;
    private List<Permission> permissions;

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
    public static class Permission{
        private Long id;
        private String path;
        private MethodPermission method;
        private String entity;
        private String name;
        private String description;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
        private Instant createdAt;
        private String createdBy;
    }
}
