package com.bulvee.ecommerce;

import com.bulvee.ecommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrder {
    public static void main(String[] args) {
        try (var orderDispacher = new KafkaDispatcher<Order>()) {
            while (true) {

                var orderId = UUID.randomUUID().toString();
                var amount = new BigDecimal(Math.random() * 10000 + 1);
                var email = Math.random() + "@gmail.com";
                var order = new Order(orderId, amount, email);

                CorrelationID id = new CorrelationID(NewOrder.class.getSimpleName());
                try {
                    orderDispacher.sendAsync(id, "ECOMMERCE_NEW_ORDER", email, order);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
