package com.example.sneaker_store.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemResponse<T> {
    private int status;
    private String message;
    private T data;
}
