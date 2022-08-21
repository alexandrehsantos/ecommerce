package com.bulvee.ecommerce.consumer;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class ServiceProvider<T> implements Callable<Void>{

    private final ServiceFactory<T> factory;
    public ServiceProvider(ServiceFactory<T> factory) {
        this.factory = factory;
    }

    public Void call() throws ExecutionException, InterruptedException, SQLException {
        var myService = factory.create();
        try(var service = new KafkaService(
                myService.getConsumerGroup(),
                Pattern.compile(myService.getTopic()),
                myService::parse,
                Map.of())){
            service.run();
        }
        return null;
    }
}
