package com.example.sneaker_store.service;

import com.example.sneaker_store.model.request.address.CreateAddressRequest;
import com.example.sneaker_store.model.request.address.UpdateAddressRequest;
import com.example.sneaker_store.model.response.address.CreateAddressResponse;
import com.example.sneaker_store.model.response.address.UpdateAddressResponse;

public interface AddressService {
    CreateAddressResponse createAddress(CreateAddressRequest req);
    UpdateAddressResponse updateAddress(UpdateAddressRequest req);
}
