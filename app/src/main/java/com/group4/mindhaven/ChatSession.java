package com.group4.mindhaven;

import java.util.List;

public class ChatSession {
    private String chatID;
    private String title;
    private List<Message> messages;
    private List<String> keywords;
    private boolean isPinned;

    public ChatSession (String chatId, String title, List<Message> messages,
                        List<String> keywords, boolean isPinned) {
        this.chatID = chatId;
        this.title = title;
        this.messages = messages;
        this.keywords = keywords;
        this.isPinned = isPinned;
    }

    public ChatSession (String chatId, String title, List<Message> messages,
                        List<String> keywords) {
        this.chatID = chatId;
        this.title = title;
        this.messages = messages;
        this.keywords = keywords;
        this.isPinned = false;
    }

    // Getter methods
    String getChatId () {
        return this.chatID;
    }

    String getTitle () {
        return this.title;
    }

    List<Message> getMessages () {
        return this.messages;
    }

    List<String> getKeywords () {
        return this.keywords;
    }

    boolean getIsPinned() {
        return this.isPinned;
    }

    // Setter methods
    void setChatId (String ID) {
        chatID = ID;
    }

    void setTitle (String title) {
        this.title = title;
    }

    void setMessages (List<Message> messages) {
        this.messages = messages;
    }

    void setKeywords (List<String> keywords) {
        this.keywords = keywords;
    }

    void setPinned (boolean isPinned) {
        this.isPinned = isPinned;
    }
}