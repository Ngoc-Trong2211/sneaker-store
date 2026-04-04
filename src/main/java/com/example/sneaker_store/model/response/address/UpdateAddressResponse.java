package com.example.sneaker_store.model.response.address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAddressResponse {
    private Long id;

    private String ward;
    private String addressLine;
    private String city;
    private boolean isDefault;
}
