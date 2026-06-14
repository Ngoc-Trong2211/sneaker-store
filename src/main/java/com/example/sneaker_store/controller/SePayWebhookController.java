package com.example.sneaker_store.controller;

import com.example.sneaker_store.config.SePayConfig;
import com.example.sneaker_store.dto.request.SePayRequest;
import com.example.sneaker_store.dto.request.order.CreateOrderRequest;
import com.example.sneaker_store.dto.response.order.SePayPaymentSessionResponse;
import com.example.sneaker_store.service.OrderService;
import com.example.sneaker_store.util.ApiMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j(topic = "SEPAY-WEBHOOK")
@RequiredArgsConstructor
@RequestMapping("/payment/v1/sepay")
public class SePayWebhookController {
    private final SePayConfig sePayConfig;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @PostMapping("/sessions")
    @ApiMessage(message = "SePay payment session created successfully")
    public ResponseEntity<SePayPaymentSessionResponse> createSession(
            @RequestBody CreateOrderRequest request,
            @CookieValue(value = "guest_id", required = false) String guestId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createSePayPaymentSession(request, guestId));
    }

    @GetMapping("/sessions/{paymentCode}")
    @ApiMessage(message = "Get SePay payment session")
    public ResponseEntity<SePayPaymentSessionResponse> getSession(@PathVariable String paymentCode) {
        return ResponseEntity.ok(orderService.getSePayPaymentSessionStatus(paymentCode));
    }

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> webhook(
            @RequestBody String rawBody,
            @RequestHeader HttpHeaders headers) {
        List<String> signatureHeaders = Arrays.asList(
                headers.getFirst("X-SePay-Signature"),
                headers.getFirst("X-Signature"),
                headers.getFirst("X-Hub-Signature-256")
        );
        if (!sePayConfig.isValid(rawBody, headers.getFirst(HttpHeaders.AUTHORIZATION), signatureHeaders)) {
            log.warn("Rejected SePay webhook because signature or API key is invalid");
            return json(HttpStatus.UNAUTHORIZED, false);
        }
        try {
            SePayRequest request = objectMapper.readValue(rawBody, SePayRequest.class);
            boolean success = orderService.confirmSePayPayment(request);
            return json(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST, success);
        } catch (Exception ex) {
            log.warn("Can not process SePay webhook: {}", ex.getMessage());
            return json(HttpStatus.BAD_REQUEST, false);
        }
    }

    private ResponseEntity<String> json(HttpStatus status, boolean success) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"success\":" + success + "}");
    }
}
