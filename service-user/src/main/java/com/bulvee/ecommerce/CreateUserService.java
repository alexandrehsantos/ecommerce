package com.bulvee.ecommerce;

import com.bulvee.ecommerce.consumer.ConsumerService;
import com.bulvee.ecommerce.consumer.ServiceRunner;
import com.bulvee.ecommerce.database.LocalDatabase;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {

    private final LocalDatabase database;

    private CreateUserService() throws SQLException {
        this.database = new LocalDatabase("users_database");
        database.createIfNotExists("create table Users (" +
                "uuid varchar(200) primary key," +
                "email varchar(200))");

    }

    public static void main(String[] args) {
        new ServiceRunner<>(CreateUserService::new).start(1);
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        var order = record.value().getPayload();
        insertNewUser(order.getEmail());
    }

    private void insertNewUser(String email) throws SQLException {
        ResultSet query = database.query("select uuid from Users " +
                "where email = ? limit 1", email);
        if (!query.next()) {
            var uuid = UUID.randomUUID().toString();
            database.update("insert into Users (uuid, email) \" +\n" +
                    "                \"values (?,?)\"", uuid, email);

            System.out.println("Usuário " + uuid + " e " + email + " adicionado");
        }
    }
}
