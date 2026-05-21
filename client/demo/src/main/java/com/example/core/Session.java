package com.example.core;

public class Session {
    private static Session instance;
    private String username;
    private Long userId;
    private String profilePicturePath;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getProfilePicturePath() { return profilePicturePath; }
    public void setProfilePicturePath(String path) { this.profilePicturePath = path; }

    public void logout() {
        this.username = null;
        this.userId = null;
        this.profilePicturePath = null;
    }

    public boolean isLoggedIn() { return this.username != null; }
}