package com.bulvee.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String orderId;
    private final BigDecimal amount;

    private final String email;

    public Order(String orderId, String userId, BigDecimal amount, String email) {
        this.orderId = orderId;
        this.amount = amount;
        this.email = email;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getEmail() {
        return email;
    }
}
