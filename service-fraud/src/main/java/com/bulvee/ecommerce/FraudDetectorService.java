package com.bulvee.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class FraudDetectorService {


    public static void main(String[] args) {
        var fraudDetectorService = new FraudDetectorService();
        try (var kafkaService = new KafkaService(FraudDetectorService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE_NEW_ORDER"),
                fraudDetectorService::parse,
                new HashMap<>()
                )) {
            kafkaService.run();
        }
    }
    private void parse(ConsumerRecord<String,Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("Processing new order.");
        System.out.println(record.key());
        System.out.println(record.timestamp());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        var message = record.value();
//        var message = record;
        var order = record.value().getPayload();
//        var order = record.value();
        // pretending that the fraud happens when the amount is >=6000
        if (isFraud(order)){
            System.out.println("Order is a fraud" + order);
            orderDispacher.send(message.getId().continueWith(FraudDetectorService.class.getSimpleName()), "ECOMMERCE_ORDER_REJECTED", order.getEmail(), message);
        } else{
            System.out.println("Approved: " + order);
            orderDispacher.send(message.getId().continueWith(FraudDetectorService.class.getSimpleName()), "ECOMMERCE_ORDER_APPROVED", order.getEmail(), message);
        }
    }

    private final KafkaDispatcher<Message<Order>> orderDispacher = new KafkaDispatcher<>();

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(BigDecimal.valueOf(6000)) >= 0;
    }
}


