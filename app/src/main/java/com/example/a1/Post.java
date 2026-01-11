package com.example.a1;

public class Post {
    private String title;
    private String location;
    private String type;
    private String question;
    private String answer;
    private String userId;
    private String imageUrl; // Added for the Image URL feature

    // Updated Constructor with 7 arguments
    public Post(String title, String location, String type, String question, String answer, String userId, String imageUrl) {
        this.title = title;
        this.location = location;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.userId = userId;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public String getUserId() { return userId; }
    public String getImageUrl() { return imageUrl; } // Added Getter
}
