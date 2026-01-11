package com.example.a1;

public class Post {
    private String title, location, type, question, answer, userId;

    public Post(String title, String location, String type, String question, String answer, String userId) {
        this.title = title;
        this.location = location;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.userId = userId;
    }

    // Getters
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public String getUserId() { return userId; }
}
