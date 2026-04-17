package com.example.sneaker_store.service;

import com.example.sneaker_store.dto.request.address.CreateAddressRequest;
import com.example.sneaker_store.dto.request.address.UpdateAddressRequest;
import com.example.sneaker_store.dto.response.address.CreateAddressResponse;
import com.example.sneaker_store.dto.response.address.GetAddressResponse;
import com.example.sneaker_store.dto.response.address.UpdateAddressResponse;

public interface AddressService {
    CreateAddressResponse createAddress(CreateAddressRequest req);
    UpdateAddressResponse updateAddress(UpdateAddressRequest req);
    void updateDefault(Long id, String userId);
    void deleteAddress(Long id);
    GetAddressResponse getAddressByUserId(String userId);
}
