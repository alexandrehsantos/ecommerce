package com.bulvee.ecommerce.dispatcher;

import com.bulvee.ecommerce.CorrelationID;
import com.bulvee.ecommerce.Message;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, Message<T>> producer;

    public KafkaDispatcher() {
        producer = new KafkaProducer<String, Message<T>>(properties());
    }

    public Future<RecordMetadata> sendAsync(CorrelationID correlationID, String topic, String key, T payload) throws InterruptedException, ExecutionException {
        var value = new Message<T>(correlationID.continueWith("_" + topic), payload);
        var record = new ProducerRecord<>(topic, key, value);

        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
            System.out.println("sucesso enviando " + data.topic() + " :::partition " + data.partition() + " /offset " + data.offset() + " /timestamp " + data.timestamp());
        };
        return producer.send(record, callback);
    }


    public void send(CorrelationID correlationID, String topic, String key, T payload) throws InterruptedException, ExecutionException {
        var value = new Message<T>(correlationID.continueWith("_" + topic), payload);
        var record = new ProducerRecord<>(topic, key, value);

        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
            System.out.println("sucesso enviando " + data.topic() + " :::partition " + data.partition() + " /offset " + data.offset() + " /timestamp " + data.timestamp());
        };
        producer.send(record, callback).get();
    }

    private Properties properties() {
        var properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");

        return properties;
    }

    @Override
    public void close() {
        producer.close();
    }
}
