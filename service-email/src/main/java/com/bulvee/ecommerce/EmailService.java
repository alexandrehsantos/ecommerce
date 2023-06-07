package com.bulvee.ecommerce;

import com.bulvee.ecommerce.consumer.ConsumerService;
import com.bulvee.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailService implements ConsumerService<Email> {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner(EmailService::new).start(5);
    }

    public void parse(ConsumerRecord<String, Message<Email>> record) {
        System.out.println("Sending email");
        System.out.println(record.key());
        Email value = record.value().getPayload();
        System.out.println("Subject: " + value.getSubject() + "Body: " + value.getBody());
        System.out.println(record.partition());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_SEND_EMAIL";
    }

    @Override
    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }
}
