package com.bulvee.ecommerce;

import com.bulvee.ecommerce.consumer.ConsumerService;
import com.bulvee.ecommerce.consumer.KafkaService;
import com.bulvee.ecommerce.consumer.ServiceRunner;
import com.bulvee.ecommerce.database.LocalDatabase;
import com.bulvee.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class FraudDetectorService implements ConsumerService<Order> {

    private final LocalDatabase database;

    FraudDetectorService() throws SQLException {
        this.database = new LocalDatabase("frauds_database");
        this.database.createIfNotExists("create table Orders(" +
                "uuid varchar(200) primary key, " +
                "is_fraud boolean)");
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner<>(FraudDetectorService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("Processing new order.");
        System.out.println(record.key());
        System.out.println(record.timestamp());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var message = record.value();
        var order = record.value().getPayload();

        if (wasProcessed(order)) {
            System.out.println("Order " + order.getId() + " was processed.");
        } else {
            System.out.println("Analysing order");
            analiseOrder(message, order);
        }
    }

    private boolean wasProcessed(Order order) throws SQLException {
        return database.query("select uuid from Orders where uuid = ? limit 1", order.getId()).next();
    }

    private void analiseOrder(Message<Order> message, Order order) throws SQLException, InterruptedException, ExecutionException {
        // pretending that the fraud happens when the amount is >=6000
        if (isFraud(order)) {
            database.update("insert into Orders  (uuid, is_fraud) values (?,true)", order.getId());
            System.out.println("Order is a fraud" + order);
            orderDispacher.send(message.getId().continueWith(FraudDetectorService.class.getSimpleName()), "ECOMMERCE_ORDER_REJECTED", order.getEmail(), message);
        } else {
            database.update("insert into Orders  (uuid, is_fraud) values (?,false)", order.getId());
            System.out.println("Approved: " + order);
            orderDispacher.send(message.getId().continueWith(FraudDetectorService.class.getSimpleName()), "ECOMMERCE_ORDER_APPROVED", order.getEmail(), message);
        }
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return this.getClass().getSimpleName();
    }

    private final KafkaDispatcher<Message<Order>> orderDispacher = new KafkaDispatcher<>();

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(BigDecimal.valueOf(6000)) >= 0;
    }
}


