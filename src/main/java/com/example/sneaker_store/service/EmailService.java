package com.example.sneaker_store.service;

import com.example.sneaker_store.model.OrderEntity;
import com.example.sneaker_store.model.OrderItemEntity;

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

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

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
            List<Map<String, Object>> items = orderItems.stream()
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

            Context context = new Context(VIETNAM_LOCALE);
            context.setVariable("order", order);
            context.setVariable("orderItems", items);
            context.setVariable("receiverName", order.getReceiverName());
            context.setVariable("orderDate", ORDER_DATE_FORMATTER.format(
                    order.getCreatedAt() == null ? Instant.now() : order.getCreatedAt()));
            context.setVariable("totalAmount", currencyFormatter.format(order.getTotalAmount()));

            String text = templateEngine.process("checkout", context);
            this.sendEmail(to, "Sneaker Store - Xác nhận đơn hàng #" + order.getCode(), text);
        } catch (Exception e) {
            log.error("Cannot build order confirmation email for order {}", order.getCode(), e);
        }
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }
}
