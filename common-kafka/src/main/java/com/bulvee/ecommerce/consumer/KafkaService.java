package com.bulvee.ecommerce.consumer;

import com.bulvee.ecommerce.Message;
import com.bulvee.ecommerce.dispatcher.GsonSerializer;
import com.bulvee.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
    private KafkaConsumer<String, Message<T>> consumer;
    private String groupId;
    private final ConsumerFunction parse;

    public KafkaService(String groupId, Pattern topic, ConsumerFunction parse, Map<String, String> overrideProperties) {
        this(parse, groupId, overrideProperties);
        this.consumer = new KafkaConsumer<String, Message<T>>(getProperties(groupId, overrideProperties));
        consumer.subscribe(topic);
    }

    public KafkaService(ConsumerFunction parse, String groupId, Map<String, String> overrideProperties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(getProperties(groupId, overrideProperties));
    }

    public void run() throws ExecutionException, InterruptedException {
        while (true) {
            var records = consumer.poll(Duration.ofMillis(1000));
            if (!records.isEmpty()) {
                consumeRecords(records);
            } else {
                logExecution();
            }
        }
    }

    private void logExecution() {
        System.out.println("================================================");
        System.out.println("No registries found...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void consumeRecords(ConsumerRecords<String, Message<T>> records) throws ExecutionException, InterruptedException {
        System.out.println("=====================================================");
        System.out.println("Found " + records.count() + " messages");
        System.out.println("=====================================================");
        try (var deadLetter = new KafkaDispatcher<>()) {
            for (var record : records) {
                try {
                    parse.consume(record);
                } catch (Exception e) {
                    e.printStackTrace();
                    var message = record.value();
                    deadLetter.send(message.getId().continueWith("DeadLetter"),
                            "ECOMMERCE_DEADLETTER",
                            message.getId().toString(),
                            new GsonSerializer().serialize("", message));
                }
            }
        }
    }

    private Properties getProperties(String groupId, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.putAll(overrideProperties);
//        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        return properties;
    }

    @Override
    public void close() {
        this.consumer.close();
    }
}
