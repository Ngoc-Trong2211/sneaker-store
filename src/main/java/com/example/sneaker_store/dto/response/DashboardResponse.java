package com.example.sneaker_store.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardResponse {
    private BigDecimal revenue;
    private Long totalProduct;
    private Long totalOrder;
    private Long totalUser;
}
