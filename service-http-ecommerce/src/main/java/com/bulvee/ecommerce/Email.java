package com.bulvee.ecommerce;

public class Email {
    private final String subject, body;

    public Email(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }
}
