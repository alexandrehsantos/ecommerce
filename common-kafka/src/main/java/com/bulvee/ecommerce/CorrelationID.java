package com.bulvee.ecommerce;

import java.util.UUID;

public class CorrelationID {

    private final String id;

    public CorrelationID(String originTitle) {
        this.id = originTitle+ "(" + UUID.randomUUID().toString() + ")";
    }

    @Override
    public String toString() {
        return "CorrelationID{" +
                "id='" + id + '\'' +
                '}';
    }
    public CorrelationID continueWith(String originTitle){
       return new CorrelationID(id + "-" + originTitle);
    }
}
