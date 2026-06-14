package com.example.sneaker_store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.util.List;

@Component
public class SePayConfig {
    @Value("${sepay.webhook-secret:}")
    private String secret;

    @Value("${sepay.webhook-api-key:}")
    private String webhookApiKey;

    @Value("${sepay.bank-code:}")
    private String bankCode;

    @Value("${sepay.bank-name:}")
    private String bankName;

    @Value("${sepay.account-number:}")
    private String accountNumber;

    @Value("${sepay.account-holder:}")
    private String accountHolder;

    @Value("${sepay.store-name:Sneaker Store}")
    private String storeName;

    public boolean isValid(String rawBody, String authorizationHeader, List<String> signatureHeaders) {
        boolean hasApiKey = hasText(webhookApiKey) || hasText(secret);
        boolean hasSecret = hasText(secret);

        if (!hasApiKey && !hasSecret) {
            return true;
        }

        if (hasApiKey && isValidApiKey(authorizationHeader)) {
            return true;
        }

        if (hasSecret && signatureHeaders != null) {
            String expectedSignature = hmacSha256(rawBody, secret);
            return signatureHeaders.stream()
                    .filter(this::hasText)
                    .anyMatch(signatureHeader -> MessageDigest.isEqual(
                            expectedSignature.getBytes(StandardCharsets.UTF_8),
                            normalizeSignature(signatureHeader).getBytes(StandardCharsets.UTF_8)
                    ));
        }

        return false;
    }

    public String createQrUrl(double amount, String description) {
        if (!isPaymentConfigured()) {
            return null;
        }
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString("https://qr.sepay.vn/img")
                .queryParam("acc", accountNumber)
                .queryParam("bank", bankCode)
                .queryParam("amount", Math.round(amount))
                .queryParam("des", toAscii(description))
                .queryParam("template", "compact")
                .queryParam("showinfo", "true");
        if (hasText(accountHolder)) {
            builder.queryParam("holder", toAscii(accountHolder).toUpperCase());
        }
        if (hasText(storeName)) {
            builder.queryParam("store", toAscii(storeName));
        }
        return builder.build().toUriString();
    }

    public boolean isPaymentConfigured() {
        return hasText(bankCode) && hasText(accountNumber);
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getBankName() {
        return hasText(bankName) ? bankName : bankCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    private boolean isValidApiKey(String authorizationHeader) {
        if (!hasText(authorizationHeader)) {
            return false;
        }
        String token = authorizationHeader.trim();
        if (token.regionMatches(true, 0, "Apikey ", 0, 7)) {
            token = token.substring(7).trim();
        }
        return secureEquals(token, webhookApiKey) || secureEquals(token, secret);
    }

    private boolean secureEquals(String left, String right) {
        if (!hasText(left) || !hasText(right)) {
            return false;
        }
        return MessageDigest.isEqual(
                left.getBytes(StandardCharsets.UTF_8),
                right.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String normalizeSignature(String signatureHeader) {
        String signature = signatureHeader.trim();
        if (signature.regionMatches(true, 0, "sha256=", 0, 7)) {
            return signature.substring(7);
        }
        return signature;
    }

    private String toAscii(String value) {
        if (!hasText(value)) {
            return "";
        }
        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('\u0110', 'D')
                .replace('\u0111', 'd');
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Can not create HMAC-SHA256 signature", e);
        }
    }
}
