package com.example.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private Long userId;
    private String username;
    private String email;
    private String profilePicturePath;

    public User() {}

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfilePicturePath() { return profilePicturePath; }

    @Override
    public String toString() { return "User [username=" + username + "]"; }
}