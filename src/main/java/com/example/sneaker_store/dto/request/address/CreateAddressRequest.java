package com.example.sneaker_store.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAddressRequest {
    @NotBlank(message = "Ward không được để trống")
    private String ward;

    @NotBlank(message = "Address line không được để trống")
    private String addressLine;

    @NotBlank(message = "City không được để trống")
    private String city;

    @NotBlank(message = "User id không được để trống")
    private String userId;

    @NotBlank(message = "Name không được để trống")
    private String name;

    @Pattern(
        regexp = "^(?:\\+84|0)[35789]\\d{8}$",
        message = "Số điện thoại không hợp lệ"
    )
    private String phone;
}
