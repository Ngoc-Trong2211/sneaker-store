package com.example.sneaker_store.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sneaker_store.model.request.address.CreateAddressRequest;
import com.example.sneaker_store.model.response.address.CreateAddressResponse;
import com.example.sneaker_store.service.AddressService;
import com.example.sneaker_store.util.ApiMessage;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Slf4j(topic = "ADDRESS-CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("/address/v1")
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/address")
    @ApiMessage(message = "Tạo address thành công")
    @Operation(summary = "Create address", description = "Tạo mới address")
    public ResponseEntity<CreateAddressResponse> postMethodName(@RequestBody @Valid CreateAddressRequest req) {
        log.info("Received request to create address");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.addressService.createAddress(req));
    }
    
}
