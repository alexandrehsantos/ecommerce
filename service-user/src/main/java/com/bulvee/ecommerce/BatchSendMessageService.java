package com.bulvee.ecommerce;

import com.bulvee.ecommerce.consumer.KafkaService;
import com.bulvee.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class BatchSendMessageService {

    private final Connection connection;

    BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users (" +
                    "uuid varchar(200) primary key, " +
                    "email varchar(200))");
        } catch (SQLException e) {
            //be careful, the sql could be wrong, be really careful
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        var batchSendMessageService = new BatchSendMessageService();
        try (var kafkaService = new KafkaService<>(BatchSendMessageService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS"),
                batchSendMessageService::parse,
                new HashMap<>())) {
            kafkaService.run();
        }
    }

    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Message<String>> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("========================================");
        System.out.println(" Processing new batch");
        System.out.println("========================================");
        var message = record.value();
        System.out.println("Topic: " + message.getPayload());
        System.out.println("========================================");

        if (true) {
            System.out.println("-----------------error---");
            throw new RuntimeException();
        }
        for (User user : getAllUsers()) {
            userDispatcher.send(message.getId().continueWith(BatchSendMessageService.class.getSimpleName()), message.getPayload(), user.getUUID(), user);
        }


    }

    private List<User> getAllUsers() throws SQLException {
        var results = connection.prepareStatement("select uuid from Users").executeQuery();
        List<User> users = new ArrayList<>();
        while (results.next()) {
            users.add(new User(results.getString(1)));
        }
        return users;
    }

    private void insertNewUser(String email) throws SQLException {
        var insert = connection.prepareStatement("insert into Users (uuid, email)" +
                "values (?,?)");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();
        System.out.println("Usuário uuid " + "e email " + email + "adicionado.");
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("select uuid from Users where email = ? limit 1");
        exists.setString(1, email);
        var results = exists.executeQuery();
        return !results.next();
    }

}
