package com.example.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private Long userId;
    private String username;
    private String email;

    public User() {}

    public User(String email, Long userId, String username) {
        this.email = email;
        this.userId = userId;
        this.username = username;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    @Override
    public String toString() {
        return "Users [username=" + username + "]";
    }
}