package com.example.sneaker_store.model.response.brand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBrandResponse {
    private Long id;
    private String name;
    private String logo;
    private String country;
}
