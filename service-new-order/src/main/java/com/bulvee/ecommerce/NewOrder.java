package com.bulvee.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;

public class NewOrder {
    public static void main(String[] args) {
        try (var orderDispacher = new KafkaDispacher<Order>()) {
            try (var emailDispacher = new KafkaDispacher<Email>()) {
                while (true) {
                    String orderTopic = "ECOMMERCE_NEW_ORDER";
                    var orderId = UUID.randomUUID().toString();
                    var userId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);
                    Order order = new Order(orderId, userId, amount);
                    orderDispacher.send(orderTopic, userId, order);

                    var email = new Email("Order","Thank you for your order! We are processing your order!");
                    String emailTopic = "ECOMMERCE_SEND_EMAIL";
                    emailDispacher.send(emailTopic, userId, email);
                }
            }
        }

    }
}
