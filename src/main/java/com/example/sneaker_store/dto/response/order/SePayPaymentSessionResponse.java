package com.example.sneaker_store.dto.response.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SePayPaymentSessionResponse {
    private String id;
    private String paymentCode;
    private String status;
    private double totalAmount;
    private String sepayQrUrl;
    private String sepayBankCode;
    private String sepayBankName;
    private String sepayAccountNumber;
    private String sepayAccountHolder;
    private String sepayTransferContent;
    private String orderCode;
}
