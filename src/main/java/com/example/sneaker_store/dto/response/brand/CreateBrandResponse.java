package com.example.sneaker_store.dto.response.brand;

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
