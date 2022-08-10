package com.bulvee.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class ReadingReportService {

    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    private final KafkaDispatcher<User> orderDispacher = new KafkaDispatcher<>();
    public static void main(String[] args) {
        var reportServie = new ReadingReportService();
        try (var kafkaService = new KafkaService(ReadingReportService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE_USER_GENERATE_READING_REPORT"),
                reportServie::parse,
                new HashMap<>()
                )) {
            kafkaService.run();
        }
    }
    private void parse(ConsumerRecord<String, Message<User>> record) throws ExecutionException, InterruptedException, IOException {
        System.out.println("-------------------------------------------------------------");
        System.out.println("Processing report for " + record.value());

        var user = record.value().getPayload();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " + user.getUUID());

        System.out.println("File created: " + target.getAbsolutePath());
    }
}

