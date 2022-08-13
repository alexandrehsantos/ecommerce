package com.bulvee.ecommerce;

import com.bulvee.ecommerce.consumer.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class EmailService {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var emailService = new EmailService();
        try (var kafkaService = new KafkaService(EmailService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE_SEND_EMAIL"),
                emailService::parse,
                new HashMap<>()
                )) {
            kafkaService.run();
        }
    }

    private void parse(ConsumerRecord<String, Email> record) {
        System.out.println("Sending email");
        System.out.println(record.key());
        Email value = record.value();
        System.out.println("Subject: " + value.getSubject() + "Body: " + value.getBody());
        System.out.println(record.partition());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
