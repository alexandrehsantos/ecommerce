package com.bulvee.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaDispacher<T> implements Closeable {

    private final KafkaProducer<String, T> producer;

    public KafkaDispacher() {
        producer = new KafkaProducer<String, T>(properties());

    }

    void send(String topic, String key, T order) {

        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
            System.out.println("sucesso enviando " + data.topic() + " :::partition " + data.partition() + " /offset " + data.offset() + " /timestamp " + data.timestamp());
        };

        var orderRecord = new ProducerRecord<String, T>(topic, key, order);

        try {
            producer.send(orderRecord, callback).get();
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        return properties;
    }

    @Override
    public void close() {
        producer.close();
    }
}
