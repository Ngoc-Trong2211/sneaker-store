package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.model.OrderEntity;
import com.example.sneaker_store.model.request.order.CreateOrderRequest;
import com.example.sneaker_store.model.response.order.CreateOrderResponse;
import com.example.sneaker_store.repository.OrderRepository;
import com.example.sneaker_store.service.OrderService;
import com.example.sneaker_store.util.enumEntity.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "ORDER-SERVICE")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request, String guestId) {
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.PENDING);
        if (guestId!=null && !guestId.isBlank()){
            order.setGuestAddress(request.getGuestAddress());
            order.setGuestName(request.getGuestName());
            order.setGuestPhone(request.getGuestPhone());
        }
        else order.setAddress(request.getAddress());
        this.orderRepository.save(order);
        return this.modelMapper.map(order, CreateOrderResponse.class);
    }
}
