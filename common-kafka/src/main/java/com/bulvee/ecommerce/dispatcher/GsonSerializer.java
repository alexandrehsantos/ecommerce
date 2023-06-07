package com.bulvee.ecommerce.dispatcher;

import com.bulvee.ecommerce.Message;
import com.bulvee.ecommerce.MessageAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Serializer;
public class GsonSerializer<T> implements Serializer {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageAdapter()).create();

    @Override
    public byte[] serialize(String s, Object object) {
        return gson.toJson(object).getBytes();
    }
}
