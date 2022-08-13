package com.bulvee.ecommerce;

import com.bulvee.ecommerce.consumer.KafkaService;
import com.bulvee.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class EmailNewOrderService {

    public static void main(String[] args) {
        var emailNewOrderService = new EmailNewOrderService();
        try (var kafkaService = new KafkaService(EmailNewOrderService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE_NEW_ORDER"),
                emailNewOrderService::parse,
                new HashMap<>())) {
            kafkaService.run();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("========================================");
        System.out.println(" Received new order, preparing email.");
        System.out.println("========================================");
        var message = record.value();
        var order = message.getPayload();
        System.out.println(" Order: " + message);

        KafkaDispatcher<String> kafkaDispatcher = new KafkaDispatcher<>();

        var emailMessage = "Thank you for your order! We are processing your order!";
        var id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        kafkaDispatcher.send(id,
                "ECOMMERCE_SEND_EMAIL",
                order.getEmail(),
                emailMessage);

    }
}
