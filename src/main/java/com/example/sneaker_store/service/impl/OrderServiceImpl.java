package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.*;
import com.example.sneaker_store.model.request.order.CreateOrderRequest;
import com.example.sneaker_store.model.response.order.CreateOrderResponse;
import com.example.sneaker_store.repository.AddressRepository;
import com.example.sneaker_store.repository.OrderItemRepository;
import com.example.sneaker_store.repository.OrderRepository;
import com.example.sneaker_store.service.OrderItemService;
import com.example.sneaker_store.service.OrderService;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.util.enumEntity.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j(topic = "ORDER-SERVICE")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final OrderItemService orderItemService;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, String guestId) {
        String email = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.PENDING);
        if (email != null && !email.equals("anonymousUser")){
            UserEntity user = this.userService.findByEmail(email);
            AddressEntity address = this.addressRepository.findByUserIdAndIsDefault(user.getId(), true);
            order.setPhone(user.getPhone());
            order.setReceiverName(user.getName());
            order.setAddress(address.getAddressLine() + ", " + address.getWard() + ", " + address.getCity());
            order.setUserId(user.getId());
        }
        else{
            order.setPhone(request.getPhone());
            order.setReceiverName(request.getReceiverName());
            order.setAddress(request.getAddress());
            order.setGuestId(guestId);
        }
        this.orderRepository.save(order);
        order.setTotalAmount(this.orderItemService.addToOrder(guestId, order));
        this.orderRepository.save(order);
        return this.modelMapper.map(order, CreateOrderResponse.class);
    }
}
