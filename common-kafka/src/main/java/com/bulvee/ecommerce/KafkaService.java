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
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
    private KafkaConsumer<String, T> consumer;
    private Class<T> type;
    private String groupId;
    private final ConsumerFunction parse;

    KafkaService(String groupId, Pattern topic, ConsumerFunction parse, Class<T> type, Map<String, String> overrideProperties) {
        this(parse,groupId,type, overrideProperties);
        this.consumer = new KafkaConsumer<String, T>(getProperties(type, groupId, overrideProperties));
        consumer.subscribe(topic);
    }

    public KafkaService(ConsumerFunction parse, String groupId, Class<T> type, Map<String, String> overrideProperties) {
        this.parse= parse;
        this.consumer = new KafkaConsumer<>(getProperties(type, groupId, overrideProperties));
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
        System.out.printf("No registries found...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void consumeRecords(ConsumerRecords<String,T> records) {
        System.out.println("Found " + records.count() + " messages");
        for (var record : records) {
            parse.consume(record);
        }
    }

    private Properties getProperties(Class<T> type, String groupId, Map<String, String> overrideProperties) {
        var properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
        properties.putAll(overrideProperties);
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        return properties;
    }

    @Override
    public void close() {
        this.consumer.close();
    }
}
