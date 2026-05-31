package com.example.sneaker_store.service;

import com.example.sneaker_store.model.OrderEntity;
import com.example.sneaker_store.model.OrderItemEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");
    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter ORDER_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(VIETNAM_ZONE);

    public void sendEmail(String to, String subject, String text){
        MimeMessage message = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mmh = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            mmh.setFrom("sneakerstore668@gmail.com", "SneakerStore");
            mmh.setTo(to);
            mmh.setSubject(subject);
            mmh.setText(text, true);
            this.javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("Cannot send email to {}", to, e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void sendOrderConfirmationEmail(String to, OrderEntity order, List<OrderItemEntity> orderItems) {
        try {
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);
            Context context = new Context(VIETNAM_LOCALE);
            setOrderContext(context, order, orderItems, currencyFormatter);

            String text = templateEngine.process("checkout", context);
            this.sendEmail(to, "Sneaker Store - Xac nhan don hang #" + order.getCode(), text);
        } catch (Exception e) {
            log.error("Cannot build order confirmation email for order {}", order.getCode(), e);
        }
    }

    @Async
    public void sendOrderStatusUpdateEmail(String to, OrderEntity order, List<OrderItemEntity> orderItems) {
        try {
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);
            Context context = new Context(VIETNAM_LOCALE);
            setOrderContext(context, order, orderItems, currencyFormatter);
            context.setVariable("isCancelled", "CANCELLED".equals(order.getStatus().name()));
            context.setVariable("cancelReason", defaultIfBlank(order.getLyDoHuy(), "Khong co ly do huy."));

            String text = templateEngine.process("order-status", context);
            this.sendEmail(to, "Sneaker Store - Cap nhat trang thai don hang #" + order.getCode(), text);
        } catch (Exception e) {
            log.error("Cannot build order status email for order {}", order.getCode(), e);
        }
    }

    private void setOrderContext(
            Context context,
            OrderEntity order,
            List<OrderItemEntity> orderItems,
            NumberFormat currencyFormatter
    ) {
        context.setVariable("order", order);
        context.setVariable("orderItems", toOrderItemContexts(orderItems, currencyFormatter));
        context.setVariable("receiverName", order.getReceiverName());
        context.setVariable("orderDate", ORDER_DATE_FORMATTER.format(
                order.getCreatedAt() == null ? Instant.now() : order.getCreatedAt()));
        context.setVariable("totalAmount", currencyFormatter.format(order.getTotalAmount()));
    }

    private List<Map<String, Object>> toOrderItemContexts(
            List<OrderItemEntity> orderItems,
            NumberFormat currencyFormatter
    ) {
        return orderItems.stream()
                .map(item -> {
                    Map<String, Object> itemContext = new HashMap<>();
                    itemContext.put("productName", emptyIfNull(item.getProductName()));
                    itemContext.put("size", emptyIfNull(item.getSize()));
                    itemContext.put("quantity", item.getQuantity());
                    itemContext.put("price", currencyFormatter.format(item.getPrice()));
                    itemContext.put("lineTotal", currencyFormatter.format(item.getPrice() * item.getQuantity()));
                    itemContext.put("percent", item.getPercent() == null ? "" : item.getPercent());
                    return itemContext;
                })
                .toList();
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
