package com.example.a1;

public class Post {
    private String title;
    private String location;
    private String description; // Added for complete data handling
    private String type;private String question;
    private String answer;
    private String userId;
    private String imageUrl;
    private String category;
    private String postId;

    // Updated Constructor with 10 arguments
    public Post(String title, String location, String description, String type,
                String question, String answer, String userId,
                String imageUrl, String category, String postId) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.category = category;
        this.postId = postId;
    }

    // Getters
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDescription() { return description; } // Added Getter
    public String getType() { return type; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public String getUserId() { return userId; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public String getPostId() { return postId; }
}
