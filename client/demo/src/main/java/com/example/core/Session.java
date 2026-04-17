package com.example.core;

public class Session {
    private static Session instance;
    private String username;

    private Session(){

    }

    public static Session getInstance(){
        if(instance == null){
            instance = new Session();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void logout(){
        this.username = null;
    }

    public boolean isLoggedIn(){
        return this.username != null;
    }
}
