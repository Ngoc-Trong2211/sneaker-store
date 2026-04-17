package com.example.sneaker_store.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemResponse<T> {
    private int status;
    private Object message;
    private T data;
}
