package com.bulvee.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String orderId;
    private final BigDecimal amount;
    private final String email;

    public Order(String userId, BigDecimal amount, String email) {
        this.orderId = userId;
        this.amount = amount;
        this.email = email;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    @Override
    public String toString() {
        return "Order{" +
                ", userId='" + orderId + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getEmail() {
        return this.email;
    }

    public String getId() {
       return this.orderId;
    }
}
