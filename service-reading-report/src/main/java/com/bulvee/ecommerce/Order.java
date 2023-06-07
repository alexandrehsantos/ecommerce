package com.bulvee.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String userId;
    private final BigDecimal amount;
    private final String email;

    public Order(String userId, BigDecimal amount, String email) {
        this.userId = userId;
        this.amount = amount;
        this.email = email;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    @Override
    public String toString() {
        return "Order{" +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getEmail() {
        return this.email;
    }
}
