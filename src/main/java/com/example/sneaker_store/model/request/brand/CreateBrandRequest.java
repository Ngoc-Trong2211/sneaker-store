package com.example.sneaker_store.model.request.brand;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBrandRequest {
    @NotBlank(message = "Name không được để trống")
    private String name;

    @NotBlank(message = "Logo không được để trống")
    private String logo;
}
