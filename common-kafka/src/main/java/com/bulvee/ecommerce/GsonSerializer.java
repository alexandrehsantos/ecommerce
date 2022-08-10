package com.bulvee.ecommerce;

import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serializer;
import com.google.gson.Gson;
public class GsonSerializer<T> implements Serializer {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();

    @Override
    public byte[] serialize(String s, Object object) {
        return gson.toJson(object).getBytes();
    }
}
