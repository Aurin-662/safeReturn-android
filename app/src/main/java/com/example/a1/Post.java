package com.example.a1;

public class Post {
    private String title;
    private String location;
    private String type; // "LOST" or "FOUND"

    public Post(String title, String location, String type) {
        this.title = title;
        this.location = location;
        this.type = type;
    }

    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getType() { return type; }
}
