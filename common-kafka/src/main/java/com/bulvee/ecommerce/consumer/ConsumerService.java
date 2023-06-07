package com.bulvee.ecommerce.consumer;

import com.bulvee.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public interface ConsumerService<T> {

    // you may argue that a ConsumerException would be better
    // and it's ok, it can better
    void parse(ConsumerRecord<String, Message<T>> record) throws IOException, ExecutionException, InterruptedException, SQLException;
    public String getTopic();
    public String getConsumerGroup();
}
