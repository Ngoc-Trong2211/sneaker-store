package com.example.sneaker_store.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sneaker_store.model.AddressEntity;
import com.example.sneaker_store.model.request.address.CreateAddressRequest;
import com.example.sneaker_store.model.response.address.CreateAddressResponse;
import com.example.sneaker_store.repository.AddressRepository;
import com.example.sneaker_store.service.AddressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "ADDRESS-SERVICE")
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService{
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    @Override
    public CreateAddressResponse createAddress(CreateAddressRequest req) {
        AddressEntity address = new AddressEntity();
        address.setWard(req.getWard());
        address.setAddressLine(req.getAddressLine());
        address.setCity(req.getCity());
        address.setDefault(false);
        this.addressRepository.save(address);
        return this.modelMapper.map(address, CreateAddressResponse.class);
    }
    
}
