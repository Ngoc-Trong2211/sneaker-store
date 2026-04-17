package com.example.sneaker_store.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "Name không được để trống")
    private String name;

    @Pattern(
        regexp = "^(?:\\+84|0)[35789]\\d{8}$",
        message = "Số điện thoại không hợp lệ"
    )
    private String phone;
}
