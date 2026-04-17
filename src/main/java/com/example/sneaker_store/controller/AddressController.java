package com.example.sneaker_store.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sneaker_store.dto.request.address.CreateAddressRequest;
import com.example.sneaker_store.dto.request.address.UpdateAddressRequest;
import com.example.sneaker_store.dto.response.address.CreateAddressResponse;
import com.example.sneaker_store.dto.response.address.GetAddressResponse;
import com.example.sneaker_store.dto.response.address.UpdateAddressResponse;
import com.example.sneaker_store.service.AddressService;
import com.example.sneaker_store.util.ApiMessage;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    
    @PutMapping("/address")
    @Operation(summary = "Update an existing address", description = "Updates an existing address with the provided details")
    @ApiMessage(message = "Address updated successfully")
    public ResponseEntity<UpdateAddressResponse> putMethodName(@RequestBody @Valid UpdateAddressRequest request) {
        log.info("Received request to update address with id '{}'", request.getId());
        return ResponseEntity.ok(this.addressService.updateAddress(request));
    }

    @PatchMapping("/address/{id}")
    @Operation(summary = "Update default address", description = "Updates default address with the provided details")
    @ApiMessage(message = "Default address updated successfully")
    public ResponseEntity<Void> updateDefault(@PathVariable Long id, @RequestParam String userId) {
        log.info("Received request to update default address with id '{}'", id);
        this.addressService.updateDefault(id, userId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @DeleteMapping("/address/{id}/remove")
    @Operation(summary = "Delete a address", description = "Delete a address with the specified ID.")
    @ApiMessage(message = "Address deleted successfully")
    public ResponseEntity<Void> deleteProductVariant(@PathVariable Long id) {
        this.addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/address/{userId}")
    @Operation(summary = "Get address by user id", description = "Get address by user id")
    @ApiMessage(message = "Get address by user id successfully")
    public ResponseEntity<GetAddressResponse> getAddressByUser(@PathVariable String userId) {
        log.info("Get address by user id '{}'", userId);
        return ResponseEntity.ok(this.addressService.getAddressByUserId(userId));
    }
}
