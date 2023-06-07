package com.bulvee.ecommerce;

public class Message<T> {

    private final CorrelationID id;
    private final T payload;

    public Message(CorrelationID id, T payload) {
        this.id = id;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", payload=" + payload +
                '}';
    }

    public T getPayload() {
        return this.payload;
    }

    public CorrelationID getId() {
        return this.id;
    }
}
