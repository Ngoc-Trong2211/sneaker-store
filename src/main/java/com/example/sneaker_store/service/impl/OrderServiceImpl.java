package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.order.SpecificationOrderRequest;
import com.example.sneaker_store.dto.response.order.GetOrderResponse;
import com.example.sneaker_store.model.*;
import com.example.sneaker_store.dto.request.order.CreateOrderRequest;
import com.example.sneaker_store.dto.response.order.CreateOrderResponse;
import com.example.sneaker_store.repository.OrderItemRepository;
import com.example.sneaker_store.repository.OrderRepository;
import com.example.sneaker_store.repository.ReviewEligibilityRepository;
import com.example.sneaker_store.service.OrderItemService;
import com.example.sneaker_store.service.OrderService;
import com.example.sneaker_store.service.UserService;
import com.example.sneaker_store.specification.OrderSpecification;
import com.example.sneaker_store.util.enumEntity.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j(topic = "ORDER-SERVICE")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final OrderItemService orderItemService;
    private final OrderItemRepository orderItemRepository;
    private final ReviewEligibilityRepository reviewEligibilityRepository;

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
                    .map(this::toOrderItemResponse).toList());
            return or;
        }).toList();
        res.setOrders(orderRes);
        return res;
    }

    @Override
    @Transactional
    public void updateStatus(String id, String status, String lyDoHuy) {
        OrderEntity order = this.orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("order not found"));
        OrderStatus newStatus = OrderStatus.valueOf(status);
        order.setStatus(newStatus);
        if (OrderStatus.CANCELLED.equals(newStatus)) {
            order.setLyDoHuy(lyDoHuy);
            order.setNguoiHuy(AuthServiceImpl.getCurrentUserLogin().orElse("anonymous"));
        }
        this.orderRepository.save(order);
        if (OrderStatus.COMPLETED.equals(newStatus)) {
            createReviewEligibilities(order);
        }
    }

    @Override
    @Transactional
    public GetOrderResponse getOrderByUser(Pageable pageable, String dateFrom, String dateTo, String status) {
        GetOrderResponse response = new GetOrderResponse();
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);
        if (email == null || "anonymousUser".equals(email)) {
            response.setOrders(Collections.emptyList());
            return response;
        }
        UserEntity user = this.userService.findByEmail(email);
        if (user == null) {
            response.setOrders(Collections.emptyList());
            return response;
        }
        OrderStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            statusEnum = OrderStatus.valueOf(status);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Instant from = null;
        Instant to = null;

        if (dateFrom != null && !dateFrom.isBlank()) {
            from = LocalDate.parse(dateFrom, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant();
        }
        if (dateTo != null && !dateTo.isBlank()) {
            to = LocalDate.parse(dateTo, formatter).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
        }
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(),
                pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderEntity> page = this.orderRepository.searchOrderByUser(user.getId(), from, to, statusEnum, sortPageable);
        List<GetOrderResponse.Order> orders = page.getContent().stream().map(order -> {
                    GetOrderResponse.Order res = this.modelMapper.map(order, GetOrderResponse.Order.class);
                    res.setOrderItems(order.getOrderItems().stream()
                                    .map(this::toOrderItemResponse).toList()
                    );
                    return res;
                }).toList();
        response.setOrders(orders);
        response.setDataPage(new GetOrderResponse.DataPage(
                        page.getNumber(), page.getSize(), page.getNumberOfElements(), page.getTotalPages()));
        return response;
    }

    @Override
    public void cancelOrder(String code, String lyDoHuy) {
        OrderEntity order = this.orderRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("order not found"));
        if (order.getStatus().equals(OrderStatus.PENDING)){
            order.setStatus(OrderStatus.CANCELLED);
            order.setLyDoHuy(lyDoHuy);
            order.setNguoiHuy(AuthServiceImpl.getCurrentUserLogin().orElse("anonymous"));
        }
        this.orderRepository.save(order);
    }

    private void createReviewEligibilities(OrderEntity order) {
        List<ReviewEligibilityEntity> eligibilities = orderItemRepository.findByOrderId(order.getId()).stream()
                .filter(item -> !reviewEligibilityRepository.existsByOrderItemId(item.getId()))
                .map(item -> {
                    String productId = resolveProductId(item);
                    ReviewEligibilityEntity eligibility = new ReviewEligibilityEntity();
                    eligibility.setUserId(order.getUserId());
                    eligibility.setProductId(productId);
                    eligibility.setOrderId(order.getId());
                    eligibility.setOrderItemId(item.getId());
                    eligibility.setStatus(false);
                    return eligibility;
                })
                .toList();
        reviewEligibilityRepository.saveAll(eligibilities);
    }

    private GetOrderResponse.Order.OrderItem toOrderItemResponse(OrderItemEntity item) {
        GetOrderResponse.Order.OrderItem response = new GetOrderResponse.Order.OrderItem();
        response.setId(item.getId());
        response.setProductId(resolveProductId(item));
        response.setProductName(item.getProductName());
        response.setSize(item.getSize());
        response.setPrice(item.getPrice());
        response.setQuantity(item.getQuantity());
        response.setUrl(getMainImageUrl(item));
        reviewEligibilityRepository.findByOrderItemId(item.getId())
                .ifPresentOrElse(eligibility -> {
                    response.setReviewStatus(eligibility.isStatus());
                    response.setCanReview(!eligibility.isStatus());
                }, () -> {
                    response.setReviewStatus(false);
                    response.setCanReview(false);
                });
        return response;
    }

    private String getMainImageUrl(OrderItemEntity item) {
        if (item.getProductVariant() == null || item.getProductVariant().getImages() == null) {
            return null;
        }
        return item.getProductVariant()
                .getImages()
                .stream()
                .filter(ProductImageEntity::isMain)
                .findFirst()
                .map(ProductImageEntity::getImageURL)
                .orElse(null);
    }

    private String resolveProductId(OrderItemEntity item) {
        if (item.getProductId() != null && !item.getProductId().isBlank()) {
            return item.getProductId();
        }
        if (item.getProductVariant() == null || item.getProductVariant().getProduct() == null) {
            throw new RuntimeException("Product not found for order item: " + item.getId());
        }
        String productId = item.getProductVariant().getProduct().getId();
        item.setProductId(productId);
        orderItemRepository.save(item);
        return productId;
    }
}
