package com.example.saveat.model;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private String timestamp;

    public ChatMessage() {
        // Default constructor untuk Firebase atau serialization
    }

    public ChatMessage(String message, boolean isUser, String timestamp) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = timestamp;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}