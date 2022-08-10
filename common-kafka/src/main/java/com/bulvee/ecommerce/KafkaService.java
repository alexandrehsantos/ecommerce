package com.bulvee.ecommerce;

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

    KafkaService(String groupId, Pattern topic, ConsumerFunction parse, Map<String, String> overrideProperties) {
        this(parse,groupId, overrideProperties);
        this.consumer = new KafkaConsumer<String, Message<T>>(getProperties(groupId, overrideProperties));
        consumer.subscribe(topic);
    }

    KafkaService(ConsumerFunction parse, String groupId, Map<String, String> overrideProperties) {
        this.parse= parse;
        this.consumer = new KafkaConsumer<>(getProperties(groupId, overrideProperties));
    }

    void run() {
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
        System.out.println("No registries found...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void consumeRecords(ConsumerRecords<String,Message<T>> records) {
        System.out.println("Found " + records.count() + " messages");
        for (var record : records) {
            try {
                parse.consume(record);
            } catch (Exception e) {
                // only catches Exception because no  matter which Exception
                // I want to recover and parse the next one
                // so far, just logging the exception for this message
                e.printStackTrace();
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
        properties.putAll(overrideProperties);
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        return properties;
    }

    @Override
    public void close() {
        this.consumer.close();
    }
}
