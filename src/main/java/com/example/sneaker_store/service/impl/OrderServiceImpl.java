package com.example.sneaker_store.service.impl;

import com.example.sneaker_store.config.SePayConfig;
import com.example.sneaker_store.dto.request.SePayRequest;
import com.example.sneaker_store.dto.request.order.SpecificationOrderRequest;
import com.example.sneaker_store.dto.response.coupon.ValidateCouponResponse;
import com.example.sneaker_store.dto.response.order.GetOrderResponse;
import com.example.sneaker_store.dto.response.order.PaymentStatusResponse;
import com.example.sneaker_store.dto.response.order.SePayPaymentSessionResponse;
import com.example.sneaker_store.model.*;
import com.example.sneaker_store.dto.request.order.CreateOrderRequest;
import com.example.sneaker_store.dto.response.order.CreateOrderResponse;
import com.example.sneaker_store.repository.OrderItemRepository;
import com.example.sneaker_store.repository.OrderRepository;
import com.example.sneaker_store.repository.CartItemRepository;
import com.example.sneaker_store.repository.CartRepository;
import com.example.sneaker_store.repository.ProductSizeRepository;
import com.example.sneaker_store.repository.ReviewEligibilityRepository;
import com.example.sneaker_store.repository.SePayPaymentSessionRepository;
import com.example.sneaker_store.repository.UserRepository;
import com.example.sneaker_store.service.EmailService;
import com.example.sneaker_store.service.CouponService;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.text.Normalizer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private final CouponService couponService;
    private final SePayConfig sePayConfig;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductSizeRepository productSizeRepository;
    private final SePayPaymentSessionRepository sePayPaymentSessionRepository;
    private static final String PAYMENT_METHOD_SEPAY = "SEPAY";
    private static final String PAYMENT_METHOD_COD = "COD";
    private static final String PAYMENT_SESSION_PENDING = "PENDING";
    private static final String PAYMENT_SESSION_PAID = "PAID";
    private static final Pattern SEPAY_PAYMENT_CODE_PATTERN = Pattern.compile("DH[A-Z0-9]{12}");

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('ORDER_CREATE') or isAnonymous() or hasAuthority('USER')")
    public CreateOrderResponse createOrder(CreateOrderRequest request, String guestId) {
        if (PAYMENT_METHOD_SEPAY.equals(normalizePaymentMethod(request.getPaymentMethod()))) {
            throw new RuntimeException("Please use SePay payment session before creating order");
        }
        String email = AuthServiceImpl.getCurrentUserLogin().isPresent() ?
                AuthServiceImpl.getCurrentUserLogin().get() : null;
        String recipientEmail = null;
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(normalizePaymentMethod(request.getPaymentMethod()));
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
        double subTotalAmount = this.orderItemService.addToOrder(guestId, order);
        double couponDiscountAmount = 0;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            couponDiscountAmount = this.couponService.useCoupon(request.getCouponCode(), subTotalAmount);
            order.setCouponCode(request.getCouponCode().trim().toUpperCase());
        }
        order.setSubTotalAmount(subTotalAmount);
        order.setCouponDiscountAmount(couponDiscountAmount);
        order.setTotalAmount(Math.max(0, subTotalAmount - couponDiscountAmount));
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
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentCode(order.getPaymentCode());
        if (PAYMENT_METHOD_SEPAY.equals(order.getPaymentMethod())) {
            response.setSepayBankCode(sePayConfig.getBankCode());
            response.setSepayBankName(sePayConfig.getBankName());
            response.setSepayAccountNumber(sePayConfig.getAccountNumber());
            response.setSepayAccountHolder(sePayConfig.getAccountHolder());
            response.setSepayTransferContent(order.getPaymentCode());
            response.setSepayQrUrl(sePayConfig.createQrUrl(order.getTotalAmount(), order.getPaymentCode()));
        }
        response.setOrderItems(orderItems.stream().map(this::toCreateOrderItemResponse).toList());
        return response;
    }

    private String normalizePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            return PAYMENT_METHOD_COD;
        }
        String normalized = paymentMethod.trim().toUpperCase();
        return PAYMENT_METHOD_SEPAY.equals(normalized) ? PAYMENT_METHOD_SEPAY : PAYMENT_METHOD_COD;
    }

    private String createSePayPaymentCode(String orderId) {
        String source = orderId == null ? String.valueOf(System.currentTimeMillis()) : orderId;
        String normalized = source.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (normalized.length() < 12) {
            normalized = (normalized + System.currentTimeMillis()).replaceAll("[^A-Za-z0-9]", "");
        }
        return "DH" + normalized.substring(0, 12);
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
    @PreAuthorize("hasAuthority('ORDER_READ')")
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
    @PreAuthorize("hasAuthority('ORDER_UPDATE_STATUS')")
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
    @PreAuthorize("hasAuthority('ORDER_READ_OWN') or hasAuthority('USER')")
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
    @PreAuthorize("hasAuthority('ORDER_CANCEL') or hasAuthority('USER')")
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

    @Override
    @Transactional
    public boolean confirmSePayPayment(SePayRequest request) {
        if (request == null || !"in".equalsIgnoreCase(request.getTransferType())) {
            return false;
        }
        Optional<SePayPaymentSessionEntity> optionalSession = findSePayPaymentSession(request);
        if (optionalSession.isEmpty()) {
            log.warn("SePay webhook ignored because payment session was not found. code={}, content={}",
                    request.getCode(), request.getContent());
            return false;
        }
        SePayPaymentSessionEntity session = optionalSession.get();
        String transactionId = resolveTransactionId(request);
        if (transactionId != null && !transactionId.isBlank()
                && sePayPaymentSessionRepository.existsBySepayTransactionId(transactionId)
                && !transactionId.equals(session.getSepayTransactionId())) {
            log.warn("SePay webhook ignored because transaction already exists. transactionId={}", transactionId);
            return true;
        }
        if (PAYMENT_SESSION_PAID.equals(session.getStatus())) {
            return true;
        }
        if (!PAYMENT_SESSION_PENDING.equals(session.getStatus())) {
            log.warn("SePay webhook ignored because payment session status is {}. paymentCode={}",
                    session.getStatus(), session.getPaymentCode());
            return false;
        }
        if (!isAmountMatched(session.getTotalAmount(), request.getTransferAmount())) {
            log.warn("SePay webhook ignored because amount does not match. paymentCode={}, expected={}, actual={}",
                    session.getPaymentCode(), session.getTotalAmount(), request.getTransferAmount());
            return false;
        }
        OrderEntity order = createOrderFromPaidSePaySession(session, transactionId);
        List<OrderItemEntity> orderItems = this.orderItemRepository.findByOrderId(order.getId());
        sendOrderStatusUpdateEmailAfterCommit(resolveRecipientEmail(order), order, orderItems);
        return true;
    }

    @Override
    @Async
    @Transactional
    public void processSePayPaymentAsync(SePayRequest request) {
        try {
            boolean success = confirmSePayPayment(request);
            if (!success) {
                log.warn("SePay webhook async processing finished without confirming payment. code={}, content={}",
                        request == null ? null : request.getCode(),
                        request == null ? null : request.getContent());
            }
        } catch (Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("Can not process SePay webhook asynchronously", ex);
        }
    }

    @Override
    public PaymentStatusResponse getPaymentStatus(String code) {
        OrderEntity order = orderRepository.findByCode(code)
                .or(() -> orderRepository.findByPaymentCode(code))
                .orElseThrow(() -> new RuntimeException("order not found"));
        boolean paid = OrderStatus.CONFIRMED.equals(order.getStatus())
                || OrderStatus.SHIPPING.equals(order.getStatus())
                || OrderStatus.COMPLETED.equals(order.getStatus());
        return new PaymentStatusResponse(
                order.getCode(),
                order.getPaymentCode(),
                order.getStatus(),
                order.getTotalAmount(),
                paid
        );
    }

    @Override
    @Transactional
    @PreAuthorize("isAnonymous() or hasAuthority('USER')")
    public SePayPaymentSessionResponse createSePayPaymentSession(CreateOrderRequest request, String guestId) {
        if (!sePayConfig.isPaymentConfigured()) {
            throw new RuntimeException("SePay payment is not configured");
        }
        String email = AuthServiceImpl.getCurrentUserLogin().orElse(null);
        String userId = null;
        String recipientEmail = request.getEmail();
        if (email != null && !"anonymousUser".equals(email)) {
            UserEntity user = this.userService.findByEmail(email);
            userId = user.getId();
            recipientEmail = user.getEmail();
        }
        CartEntity cart = resolveCheckoutCart(userId, guestId);
        List<CartItemEntity> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        SePayPaymentSessionEntity session = new SePayPaymentSessionEntity();
        session.setPaymentCode(createUniqueSePayPaymentCode());
        session.setStatus(PAYMENT_SESSION_PENDING);
        session.setCartId(cart.getId());
        session.setUserId(userId);
        session.setGuestId(guestId);
        session.setEmail(recipientEmail);
        session.setPhone(request.getPhone());
        session.setReceiverName(request.getReceiverName());
        session.setAddress(request.getAddress());

        List<SePayPaymentSessionItemEntity> items = cartItems.stream()
                .map(cartItem -> toSePayPaymentSessionItem(session, cartItem))
                .collect(Collectors.toList());
        double subTotalAmount = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        double couponDiscountAmount = 0;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            ValidateCouponResponse coupon = couponService.validateCoupon(request.getCouponCode(), subTotalAmount);
            if (!coupon.isValid()) {
                throw new RuntimeException(coupon.getMessage());
            }
            session.setCouponCode(coupon.getCode());
            couponDiscountAmount = coupon.getDiscountAmount();
        }
        session.setSubTotalAmount(subTotalAmount);
        session.setCouponDiscountAmount(couponDiscountAmount);
        session.setTotalAmount(Math.max(0, subTotalAmount - couponDiscountAmount));
        session.setItems(items);
        SePayPaymentSessionEntity saved = sePayPaymentSessionRepository.save(session);
        return toSePayPaymentSessionResponse(saved);
    }

    @Override
    public SePayPaymentSessionResponse getSePayPaymentSessionStatus(String paymentCode) {
        SePayPaymentSessionEntity session = sePayPaymentSessionRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new RuntimeException("payment session not found"));
        return toSePayPaymentSessionResponse(session);
    }

    private SePayPaymentSessionItemEntity toSePayPaymentSessionItem(
            SePayPaymentSessionEntity session,
            CartItemEntity cartItem) {
        ProductVariantEntity variant = cartItem.getProductVariant();
        if (variant.getStock() < cartItem.getQuantity()) {
            throw new RuntimeException(variant.getProduct().getName() + " out of stock");
        }
        ProductSizeEntity size = this.productSizeRepository.findById(cartItem.getIdSize())
                .orElseThrow(() -> new RuntimeException("size item not found"));
        if (size.getQuantity() < cartItem.getQuantity()) {
            throw new RuntimeException("Size out of stock");
        }

        SePayPaymentSessionItemEntity item = new SePayPaymentSessionItemEntity();
        item.setPaymentSession(session);
        item.setProductVariant(variant);
        item.setProductId(variant.getProduct().getId());
        item.setProductName(variant.getProduct().getName());
        item.setQuantity(cartItem.getQuantity());
        item.setSize(cartItem.getSize());
        item.setIdSize(cartItem.getIdSize());
        item.setPrice(resolveCartItemPrice(cartItem));
        item.setPercent(resolveDiscountPercent(variant.getProduct()));
        return item;
    }

    private double resolveCartItemPrice(CartItemEntity cartItem) {
        ProductEntity product = cartItem.getProductVariant().getProduct();
        double price = product.getPrice();
        DiscountEntity discount = product.getDiscount();
        Instant now = Instant.now();
        if (discount != null && discount.getStartTime().isBefore(now) && discount.getEndTime().isAfter(now)) {
            return price - (price * discount.getPercent()) / 100.0;
        }
        return price;
    }

    private Integer resolveDiscountPercent(ProductEntity product) {
        DiscountEntity discount = product.getDiscount();
        Instant now = Instant.now();
        if (discount != null && discount.getStartTime().isBefore(now) && discount.getEndTime().isAfter(now)) {
            return discount.getPercent();
        }
        return null;
    }

    private CartEntity resolveCheckoutCart(String userId, String guestId) {
        if (userId != null && !userId.isBlank()) {
            return cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
        }
        if (guestId == null || guestId.isBlank()) {
            throw new RuntimeException("Guest id is required");
        }
        return cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    private String createUniqueSePayPaymentCode() {
        String paymentCode;
        do {
            paymentCode = "DH" + UUID.randomUUID().toString()
                    .replace("-", "")
                    .toUpperCase()
                    .substring(0, 12);
        } while (sePayPaymentSessionRepository.findByPaymentCode(paymentCode).isPresent());
        return paymentCode;
    }

    private SePayPaymentSessionResponse toSePayPaymentSessionResponse(SePayPaymentSessionEntity session) {
        String transferContent = createSePayTransferContent(session.getPaymentCode());
        SePayPaymentSessionResponse response = new SePayPaymentSessionResponse();
        response.setId(session.getId());
        response.setPaymentCode(session.getPaymentCode());
        response.setStatus(session.getStatus());
        response.setTotalAmount(session.getTotalAmount());
        response.setSepayBankCode(sePayConfig.getBankCode());
        response.setSepayBankName(sePayConfig.getBankName());
        response.setSepayAccountNumber(sePayConfig.getAccountNumber());
        response.setSepayAccountHolder(sePayConfig.getAccountHolder());
        response.setSepayTransferContent(transferContent);
        response.setSepayQrUrl(sePayConfig.createQrUrl(session.getTotalAmount(), transferContent));
        if (session.getOrderId() != null) {
            orderRepository.findById(session.getOrderId())
                    .ifPresent(order -> response.setOrderCode(order.getCode()));
        }
        return response;
    }

    private String createSePayTransferContent(String paymentCode) {
        return "THANH TOAN " + paymentCode;
    }

    private Optional<OrderEntity> findSePayOrder(SePayRequest request) {
        if (request.getCode() != null && !request.getCode().isBlank()) {
            Optional<OrderEntity> byPaymentCode = orderRepository.findByPaymentCode(request.getCode().trim());
            if (byPaymentCode.isPresent()) {
                return byPaymentCode;
            }
            Optional<OrderEntity> byOrderCode = orderRepository.findByCode(request.getCode().trim());
            if (byOrderCode.isPresent()) {
                return byOrderCode;
            }
        }
        return extractPaymentCode(request.getContent())
                .or(() -> extractPaymentCode(request.getDescription()))
                .flatMap(orderRepository::findByPaymentCode);
    }

    private Optional<String> extractPaymentCode(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        Matcher matcher = SEPAY_PAYMENT_CODE_PATTERN.matcher(value.toUpperCase());
        if (matcher.find()) {
            return Optional.of(matcher.group());
        }
        return Optional.empty();
    }

    private boolean isAmountMatched(double expected, BigDecimal actual) {
        if (actual == null) {
            return false;
        }
        BigDecimal expectedAmount = BigDecimal.valueOf(expected).setScale(0, RoundingMode.HALF_UP);
        BigDecimal actualAmount = actual.setScale(0, RoundingMode.HALF_UP);
        return expectedAmount.compareTo(actualAmount) == 0;
    }

    private String resolveTransactionId(SePayRequest request) {
        if (request.getId() != null) {
            return String.valueOf(request.getId());
        }
        return request.getReferenceCode();
    }

    private Optional<SePayPaymentSessionEntity> findSePayPaymentSession(SePayRequest request) {
        if (request.getCode() != null && !request.getCode().isBlank()) {
            Optional<SePayPaymentSessionEntity> byPaymentCode =
                    sePayPaymentSessionRepository.findByPaymentCode(request.getCode().trim());
            if (byPaymentCode.isPresent()) {
                return byPaymentCode;
            }
        }
        return extractPaymentCode(request.getContent())
                .or(() -> extractPaymentCode(request.getDescription()))
                .flatMap(sePayPaymentSessionRepository::findByPaymentCode);
    }

    private OrderEntity createOrderFromPaidSePaySession(SePayPaymentSessionEntity session, String transactionId) {
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(PAYMENT_METHOD_SEPAY);
        order.setPaymentCode(session.getPaymentCode());
        order.setSepayTransactionId(transactionId);
        order.setPaidAt(Instant.now());
        order.setUserId(session.getUserId());
        order.setGuestId(session.getGuestId());
        order.setEmail(session.getEmail());
        order.setPhone(session.getPhone());
        order.setReceiverName(session.getReceiverName());
        order.setAddress(session.getAddress());
        order.setCode(createCodeOrder(session.getAddress(), session.getPhone(), session.getReceiverName()));
        order.setSubTotalAmount(session.getSubTotalAmount());
        order.setCouponDiscountAmount(session.getCouponDiscountAmount());
        order.setCouponCode(session.getCouponCode());
        order.setTotalAmount(session.getTotalAmount());
        order = orderRepository.save(order);

        for (SePayPaymentSessionItemEntity item : session.getItems()) {
            createOrderItemFromPaymentSessionItem(order, item);
        }
        if (session.getCouponCode() != null && !session.getCouponCode().isBlank()) {
            tryConsumeCoupon(session);
        }
        if (session.getCartId() != null && !session.getCartId().isBlank()) {
            cartItemRepository.deleteAllByCartId(session.getCartId());
        }
        session.setStatus(PAYMENT_SESSION_PAID);
        session.setSepayTransactionId(transactionId);
        session.setPaidAt(order.getPaidAt());
        session.setOrderId(order.getId());
        sePayPaymentSessionRepository.save(session);
        return order;
    }

    private void createOrderItemFromPaymentSessionItem(OrderEntity order, SePayPaymentSessionItemEntity item) {
        ProductVariantEntity variant = item.getProductVariant();
        if (variant.getStock() < item.getQuantity()) {
            throw new RuntimeException(variant.getProduct().getName() + " out of stock");
        }
        ProductSizeEntity size = productSizeRepository.findById(item.getIdSize())
                .orElseThrow(() -> new RuntimeException("size item not found"));
        if (size.getQuantity() < item.getQuantity()) {
            throw new RuntimeException("Size out of stock");
        }
        size.setQuantity(size.getQuantity() - item.getQuantity());
        variant.setStock(variant.getStock() - item.getQuantity());
        ProductEntity product = variant.getProduct();
        product.setQuantity(product.getQuantity() - item.getQuantity());

        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrder(order);
        orderItem.setProductVariant(variant);
        orderItem.setProductId(item.getProductId());
        orderItem.setProductName(item.getProductName());
        orderItem.setQuantity(item.getQuantity());
        orderItem.setSize(item.getSize());
        orderItem.setPrice(item.getPrice());
        orderItem.setPercent(item.getPercent());
        orderItemRepository.save(orderItem);
    }

    private void tryConsumeCoupon(SePayPaymentSessionEntity session) {
        try {
            couponService.useCoupon(session.getCouponCode(), session.getSubTotalAmount());
        } catch (Exception ex) {
            log.warn("Can not consume coupon after paid SePay session. paymentCode={}, coupon={}, message={}",
                    session.getPaymentCode(), session.getCouponCode(), ex.getMessage());
        }
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
