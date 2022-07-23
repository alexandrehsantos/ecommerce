package com.bulvee.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String orderId, userId;
    private final BigDecimal amount;

    public Order(String orderId, String userId, BigDecimal amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }
}
