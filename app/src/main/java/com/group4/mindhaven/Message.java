package com.group4.mindhaven;

public class Message {
    public enum MessageType {
        USER,
        AI
    }

    private final String content;
    private final MessageType type;

    public Message(String content, MessageType type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public MessageType getType() {
        return type;
    }
}