package com.example.sneaker_store.model.request.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAddressRequest {
    @NotNull(message = "Id khong duoc de trong")
    private Long id;

    @NotBlank(message = "Ward không được để trống")
    private String ward;

    @NotBlank(message = "Address line không được để trống")
    private String addressLine;

    @NotBlank(message = "City không được để trống")
    private String city;
}
