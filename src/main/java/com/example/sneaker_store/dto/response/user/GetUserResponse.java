package com.example.sneaker_store.dto.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class GetUserResponse {
    private DataPage page;
    private List<User> users;

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
    public static class User{
        private String id;
        private String name;
        private String email;
        private String status;
        private String role;
        private String phone;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
        private Instant createdAt;
        private String createdBy;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
        private Instant updatedAt;
        private String updatedBy;
    }
}
