package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.dto.request.order.SpecificationOrderRequest;
import com.example.sneaker_store.dto.response.order.GetOrderResponse;
import com.example.sneaker_store.model.*;
import com.example.sneaker_store.dto.request.order.CreateOrderRequest;
import com.example.sneaker_store.dto.response.order.CreateOrderResponse;
import com.example.sneaker_store.repository.OrderItemRepository;
import com.example.sneaker_store.repository.OrderRepository;
import com.example.sneaker_store.repository.ReviewEligibilityRepository;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.EmailService;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.text.Normalizer;
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
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request, String guestId) {
        String email = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
        String recipientEmail = null;
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.PENDING);
        if (email != null && !email.equals("anonymousUser")){
            UserEntity user = this.userService.findByEmail(email);
            recipientEmail = user.getEmail();
            order.setPhone(request.getPhone());
            order.setReceiverName(request.getReceiverName());
            order.setAddress(request.getAddress());
            order.setUserId(user.getId());
            order.setEmail(email);
            order.setCode(createCodeOrder(request.getAddress(), request.getPhone(), request.getReceiverName()));
        }
        else{
            order.setPhone(request.getPhone());
            order.setReceiverName(request.getReceiverName());
            order.setAddress(request.getAddress());
            order.setGuestId(guestId);
            order.setEmail(request.getEmail());
            order.setCode(createCodeOrder(request.getAddress(), request.getPhone(), request.getReceiverName()));
            recipientEmail = request.getEmail();
        }
        order = this.orderRepository.save(order);
        order.setTotalAmount(this.orderItemService.addToOrder(guestId, order));
        order = this.orderRepository.save(order);
        List<OrderItemEntity> orderItems = this.orderItemRepository.findByOrderId(order.getId());
        sendOrderConfirmationEmailAfterCommit(recipientEmail, order, orderItems);
        return toCreateOrderResponse(order, orderItems, recipientEmail);
    }

    private void sendOrderConfirmationEmailAfterCommit(String recipientEmail, OrderEntity order, List<OrderItemEntity> orderItems) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            log.warn("Skip order confirmation email because recipient email is empty. orderCode={}", order.getCode());
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    emailService.sendOrderConfirmationEmail(recipientEmail, order, orderItems);
                }
            });
            return;
        }
        emailService.sendOrderConfirmationEmail(recipientEmail, order, orderItems);
    }

    private CreateOrderResponse toCreateOrderResponse(OrderEntity order, List<OrderItemEntity> orderItems, String email) {
        CreateOrderResponse response = this.modelMapper.map(order, CreateOrderResponse.class);
        response.setCode(order.getCode());
        response.setEmail(email == null ? order.getEmail() : email);
        response.setPhone(order.getPhone());
        response.setReceiverName(order.getReceiverName());
        response.setGuestPhone(order.getPhone());
        response.setGuestName(order.getReceiverName());
        response.setOrderItems(orderItems.stream().map(this::toCreateOrderItemResponse).toList());
        return response;
    }

    private CreateOrderResponse.OrderItem toCreateOrderItemResponse(OrderItemEntity item) {
        CreateOrderResponse.OrderItem response = new CreateOrderResponse.OrderItem();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setProductName(item.getProductName());
        response.setQuantity(item.getQuantity());
        response.setSize(item.getSize());
        response.setPrice(item.getPrice());
        response.setPercent(item.getPercent());
        return response;
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
        if (input == null || input.isBlank()) {
            return "NA";
        }
        String normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('\u0110', 'D')
                .replace('\u0111', 'd')
                .toUpperCase()
                .replaceAll("[^A-Z0-9]", "");
        if (normalized.isBlank()) {
            return "NA";
        }
        return normalized.substring(0, Math.min(5, normalized.length()));
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
        List<OrderItemEntity> orderItems = this.orderItemRepository.findByOrderId(order.getId());
        sendOrderStatusUpdateEmailAfterCommit(resolveRecipientEmail(order), order, orderItems);
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
    @Transactional
    public void cancelOrder(String code, String lyDoHuy) {
        OrderEntity order = this.orderRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("order not found"));
        if (order.getStatus().equals(OrderStatus.PENDING)){
            order.setStatus(OrderStatus.CANCELLED);
            order.setLyDoHuy(lyDoHuy);
            order.setNguoiHuy(AuthServiceImpl.getCurrentUserLogin().orElse("anonymous"));
            this.orderRepository.save(order);
            List<OrderItemEntity> orderItems = this.orderItemRepository.findByOrderId(order.getId());
            sendOrderStatusUpdateEmailAfterCommit(resolveRecipientEmail(order), order, orderItems);
            return;
        }
        this.orderRepository.save(order);
    }

    private void sendOrderStatusUpdateEmailAfterCommit(String recipientEmail, OrderEntity order, List<OrderItemEntity> orderItems) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            log.warn("Skip order status email because recipient email is empty. orderCode={}", order.getCode());
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    emailService.sendOrderStatusUpdateEmail(recipientEmail, order, orderItems);
                }
            });
            return;
        }
        emailService.sendOrderStatusUpdateEmail(recipientEmail, order, orderItems);
    }

    private String resolveRecipientEmail(OrderEntity order) {
        if (order.getEmail() != null && !order.getEmail().isBlank()) {
            return order.getEmail();
        }
        if (order.getUserId() == null || order.getUserId().isBlank()) {
            return null;
        }
        return userRepository.findById(order.getUserId())
                .map(UserEntity::getEmail)
                .orElse(null);
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
        if (item.getPercent()!=null) response.setPercent(item.getPercent());
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
