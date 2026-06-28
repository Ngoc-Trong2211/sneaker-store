package com.example.sneaker_store.dto.request.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class    UpdateBrandRequest {

    @NotNull(message = "ID thương hiệu không được để trống")
    private Long id;

    @NotBlank(message = "Tên thương hiệu không được để trống")
    private String name;

    @NotBlank(message = "Logo không được để trống")
    private String logo;

    @NotBlank(message = "Mã định danh ảnh không được để trống")
    private String publicId;

    @NotBlank(message = "Quốc gia không được để trống")
    private String countryCode;
}
