package com.example.sneaker_store.dto.request.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAddressRequest {
    @NotBlank(message = "Phường/xã không được để trống")
    private String ward;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    private String addressLine;

    @NotBlank(message = "Tỉnh/thành phố không được để trống")
    private String city;

    private String userId;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String name;

    @Pattern(
        regexp = "^(?:\\+84|0)[35789]\\d{8}$",
        message = "Số điện thoại không hợp lệ"
    )
    private String phone;
}
