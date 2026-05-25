package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.order.SpecificationOrderRequest;
import com.example.sneaker_store.dto.response.order.GetOrderResponse;
import com.example.sneaker_store.model.*;
import com.example.sneaker_store.dto.request.order.CreateOrderRequest;
import com.example.sneaker_store.dto.response.order.CreateOrderResponse;
import com.example.sneaker_store.repository.AddressRepository;
import com.example.sneaker_store.repository.OrderItemRepository;
import com.example.sneaker_store.repository.OrderRepository;
import com.example.sneaker_store.service.OrderItemService;
import com.example.sneaker_store.service.OrderService;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.specification.OrderSpecification;
import com.example.sneaker_store.util.enumEntity.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, String guestId) {
        String email = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.PENDING);
        if (email != null && !email.equals("anonymousUser")){
            UserEntity user = this.userService.findByEmail(email);
            order.setPhone(request.getPhone());
            order.setReceiverName(request.getReceiverName());
            order.setAddress(request.getAddress());
            order.setUserId(user.getId());
            order.setCode(createCodeOrder(request.getAddress(), request.getPhone(), request.getReceiverName()));
        }
        else{
            order.setPhone(request.getPhone());
            order.setReceiverName(request.getReceiverName());
            order.setAddress(request.getAddress());
            order.setGuestId(guestId);
            order.setCode(createCodeOrder(request.getAddress(), request.getPhone(), request.getReceiverName()));
        }
        order = this.orderRepository.save(order);
        order.setTotalAmount(this.orderItemService.addToOrder(guestId, order));
        this.orderRepository.save(order);
        return this.modelMapper.map(order, CreateOrderResponse.class);
    }

    private String createCodeOrder(String address, String phone, String name){
        String addressCode = toCode(address);
        String phoneCode = toCode(phone);
        String nameCode = toCode(name);
        return String.format("%s-%s-%s-%s",
                addressCode,
                phoneCode,
                nameCode,
                System.currentTimeMillis()
        );
    }

    private static String toCode(String input) {
        return input.trim()
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", "")
                .substring(0, Math.min(5, input.length()));
    }

    @Override
    public GetOrderResponse getOrder(Pageable pageable, SpecificationOrderRequest req) {
        Specification<OrderEntity> spec = OrderSpecification.specOrder(req);
        Page<OrderEntity> page = this.orderRepository.findAll(spec, pageable);
        GetOrderResponse res = new GetOrderResponse();
        GetOrderResponse.DataPage pageRes = this.modelMapper.map(page, GetOrderResponse.DataPage.class);
        res.setDataPage(pageRes);
        List<GetOrderResponse.Order> orderRes = page.getContent().stream().map(order ->{
            GetOrderResponse.Order or = this.modelMapper.map(order, GetOrderResponse.Order.class);
            or.setOrderItems(order.getOrderItems().stream()
                    .map(item -> {
                        GetOrderResponse.Order.OrderItem ori = new GetOrderResponse.Order.OrderItem();
                        ori.setProductName(item.getProductName());
                        ori.setSize(item.getSize());
                        ori.setPrice(item.getPrice());
                        ori.setQuantity(item.getQuantity());
                        ori.setUrl(item.getProductVariant().getImages().stream().filter(ProductImageEntity::isMain)
                                .findFirst().map(ProductImageEntity::getImageURL).orElse(null));
                        return ori;
                    }).toList());
            return or;
        }).toList();
        res.setOrders(orderRes);
        return res;
    }

    @Override
    public void updateStatus(String id, String status) {
        OrderEntity order = this.orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("order not found"));
        order.setStatus(OrderStatus.valueOf(status));
        this.orderRepository.save(order);
    }
}
