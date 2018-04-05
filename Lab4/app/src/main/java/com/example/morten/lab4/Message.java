package com.example.morten.lab4;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class Message {

    public String message;
    public String author;
    public String messageId;
    public String messageTime;
    public String randomId;

    public Message() {}

    public Message(String messageText, String messageUser) {
        this.message = messageText;
        this.author = messageUser;
        this.messageId = getRandomId();

        // Initialize to current time
        messageTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }

    public String getRandomId() {
        String id = UUID.randomUUID().toString();
        id = id.replace("-", "");
        return id;
    }

    public String getMessageText() {
        return message;
    }

    public String getMessageUser() {
        return author;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageTime() {
        return messageTime;
    }

}
