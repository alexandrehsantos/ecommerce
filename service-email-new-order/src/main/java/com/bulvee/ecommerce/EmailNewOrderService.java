package com.bulvee.ecommerce;

import com.bulvee.ecommerce.consumer.ConsumerService;
import com.bulvee.ecommerce.consumer.ServiceRunner;
import com.bulvee.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

    public static void main(String[] args) {
        new ServiceRunner(EmailNewOrderService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("========================================");
        System.out.println(" Received new order, preparing email.");
        System.out.println("========================================");
        var message = record.value();
        var order = message.getPayload();
        System.out.println(" Order: " + message);

        KafkaDispatcher<Email> kafkaDispatcher = new KafkaDispatcher<>();

        var id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        var emailMessage = new Email("Order", "Thank you for your order! We are processing your order!");
        kafkaDispatcher.send(id, "ECOMMERCE_SEND_EMAIL", order.getEmail(), emailMessage);

    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return this.getClass().getSimpleName();
    }
}
