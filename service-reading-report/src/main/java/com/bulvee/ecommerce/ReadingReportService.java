package com.bulvee.ecommerce;

import com.bulvee.ecommerce.consumer.ConsumerService;
import com.bulvee.ecommerce.consumer.ServiceRunner;
import com.bulvee.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class ReadingReportService implements ConsumerService<User> {

    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();

    private final KafkaDispatcher<User> orderDispacher = new KafkaDispatcher<>();
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner<User>(ReadingReportService::new).start(5);
    }
    @Override
    public void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        System.out.println("-------------------------------------------------------------");
        System.out.println("Processing report for " + record.value());

        var user = record.value().getPayload();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Created for " + user.getUUID());

        System.out.println("File created: " + target.getAbsolutePath());
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_USER_GENERATE_READING_REPORT";
    }

    @Override
    public String getConsumerGroup() {
        return this.getClass().getSimpleName();
    }
}


