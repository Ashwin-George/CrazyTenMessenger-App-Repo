package com.example.android.crazytenmessenger.auth;

public class User {
    private String username;
    private String email;
    private String photoUri;

    public User(){}

    public User(String username, String email,String photoUri){
        this.username = username;
        this.email = email;
        this.photoUri=photoUri;
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
