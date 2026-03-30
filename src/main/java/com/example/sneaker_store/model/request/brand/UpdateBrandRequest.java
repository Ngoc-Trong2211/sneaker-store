package com.example.sneaker_store.model.request.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBrandRequest {
    @NotNull(message = "id khong duoc de trong")
    private Long id;

    @NotBlank(message = "Name không được để trống")
    private String name;

    @NotBlank(message = "Logo không được để trống")
    private String logo;
}
