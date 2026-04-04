package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.address.CreateAddressRequest;
import com.example.sneaker_store.model.response.address.CreateAddressResponse;

public interface AddressService {
    CreateAddressResponse createAddress(CreateAddressRequest req);
}
