package com.bulvee.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrder {
    public static void main(String[] args) {
        try (var orderDispacher = new KafkaDispatcher<Order>()) {
            try (var emailDispacher = new KafkaDispatcher<Email>()) {
                while (true) {

                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 10000 + 1);
                    var email = Math.random() + "@gmail.com";
                    var order = new Order(orderId,  amount, email);

                    try {
                        orderDispacher.send(new CorrelationID(NewOrder.class.getSimpleName()), "ECOMMERCE_NEW_ORDER", email, order);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }

                    var emailMessage = new Email("Order","Thank you for your order! We are processing your order!");
                    String emailTopic = "ECOMMERCE_SEND_EMAIL";
                    try {
                        emailDispacher.send(new CorrelationID(NewOrder.class.getSimpleName()), emailTopic, email, emailMessage);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
                }
            }
        }
    }
}
