package com.bulvee.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FraudDetectorService {

    public static void main(String[] args) {
        var fraudDetectorService = new FraudDetectorService();
        try (var kafkaService = new KafkaService(FraudDetectorService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE_NEW_ORDER"),
                fraudDetectorService::parse,
                Order.class,
                new HashMap<>()
                )) {
            kafkaService.run();
        }
    }
    private void parse(ConsumerRecord<String, Order> record) {
        System.out.println("Processing new order.");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.timestamp());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}


