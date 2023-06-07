package com.bulvee.ecommerce.consumer;

import java.util.concurrent.Executors;

public class ServiceRunner<T> {
    private final ServiceProvider<T> provider;

    public ServiceRunner(ServiceFactory<T> factory) {
        this.provider = new ServiceProvider<T>(factory);
    }


    public void start(int threadCound) {
        var pool = Executors.newFixedThreadPool(threadCound);
        for(int i =0; i <= threadCound; i++){
            pool.submit(provider);
        }
    }
}
