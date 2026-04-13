package com.example.sneaker_store.model.request.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecificationUserRequest {
    private String keySearch;
    private String status;
    private int roleId;
}
