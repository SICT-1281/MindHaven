package com.group4.mindhaven;

public class Book {
    public enum MessageType {
        USER,
        AI
    }

    private final String title;
    private final int imageResId;
    private final String link;

    private String name;

    public Book(String title, int imageResId, String link, String name) {
        this.title = title;
        this.imageResId = imageResId;
        this.link = link;
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }
}