package com.example.sneaker_store.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.sneaker_store.model.AddressEntity;
import com.example.sneaker_store.model.UserEntity;
import com.example.sneaker_store.dto.request.address.CreateAddressRequest;
import com.example.sneaker_store.dto.request.address.UpdateAddressRequest;
import com.example.sneaker_store.dto.response.address.CreateAddressResponse;
import com.example.sneaker_store.dto.response.address.GetAddressResponse;
import com.example.sneaker_store.dto.response.address.UpdateAddressResponse;
import com.example.sneaker_store.repository.AddressRepository;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.AddressService;
import com.example.sneaker_store.util.exception.user.IdInvalidException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "ADDRESS-SERVICE")
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService{
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    private UserEntity getCurrentUser() {
        String email = AuthServiceImpl.getCurrentUserLogin()
            .orElseThrow(() -> new IdInvalidException("Không xác định được người dùng đang đăng nhập"));
        return this.userRepository.findByEmail(email)
            .orElseThrow(() -> new IdInvalidException("Người dùng không tồn tại"));
    }

    private void validateAddressOwner(AddressEntity address, UserEntity user) {
        if (address.getUser() == null || !address.getUser().getId().equals(user.getId())) {
            throw new IdInvalidException("Bạn không có quyền thao tác với địa chỉ này");
        }
    }

    @Override
    @PreAuthorize("hasAuthority('USER')")
    public CreateAddressResponse createAddress(CreateAddressRequest req) {
        UserEntity user = getCurrentUser();
        AddressEntity address = new AddressEntity();
        if (this.addressRepository.existsByWardAndAddressLineAndCityAndUserId
            (req.getWard(), req.getAddressLine(), req.getCity(), user.getId())) {
            throw new RuntimeException("Địa chỉ này đã tồn tại");
        }
        address.setWard(req.getWard());
        address.setAddressLine(req.getAddressLine());
        address.setCity(req.getCity());
        if (user.getAddress().isEmpty()) address.setDefault(true);
        else address.setDefault(false);
        address.setName(req.getName());
        address.setPhone(req.getPhone());
        address.setUser(user);
        this.addressRepository.save(address);
        return this.modelMapper.map(address, CreateAddressResponse.class);
    }

    @Override
    @PreAuthorize("hasAuthority('USER')")
    public UpdateAddressResponse updateAddress(UpdateAddressRequest req) {
        UserEntity user = getCurrentUser();
        AddressEntity address = this.addressRepository.findById(req.getId())
            .orElseThrow(() -> new IdInvalidException("Địa chỉ không tồn tại"));
        validateAddressOwner(address, user);
        address.setWard(req.getWard());
        address.setAddressLine(req.getAddressLine());
        address.setCity(req.getCity());
        this.addressRepository.save(address);
        return this.modelMapper.map(address, UpdateAddressResponse.class);
    }

    @Override
    @PreAuthorize("hasAuthority('USER')")
    public void updateDefault(Long id, String userId) {
        UserEntity user = getCurrentUser();
        AddressEntity address = this.addressRepository.findById(id)
            .orElseThrow(() -> new IdInvalidException("Địa chỉ không tồn tại"));
        validateAddressOwner(address, user);
        user.getAddress().forEach(item -> {
            item.setDefault(false);
            this.addressRepository.save(item);
        });
        address.setDefault(true);
        this.addressRepository.save(address);
    }

    @Override
    @PreAuthorize("hasAuthority('USER')")
    public void deleteAddress(Long id) {
        UserEntity user = getCurrentUser();
        AddressEntity address = this.addressRepository.findById(id)
            .orElseThrow(() -> new IdInvalidException("Địa chỉ không tồn tại"));
        validateAddressOwner(address, user);
        this.addressRepository.deleteById(address.getId());
    }

    @Override
    @PreAuthorize("hasAuthority('USER')")
    public GetAddressResponse getAddressByUserId(String userId) {
        UserEntity user = getCurrentUser();
        List<AddressEntity> address = this.addressRepository.findByUserId(user.getId());
        GetAddressResponse res = new GetAddressResponse();
        res.setAddress(address.stream().map(add -> this.modelMapper.map(add, GetAddressResponse.Address.class)).toList());
        return res;
    }
}
