package com.example.sneaker_store.dto.response.address;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GetAddressResponse {
    private List<Address> address;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address{
        private String name;
        private String phone;
        private String ward;
        private String addressLine;
        private String city;
        private boolean isDefault;
    }
}
