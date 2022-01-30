package com.example.android.crazytenmessenger;

import androidx.annotation.Nullable;

public class Message {
    private String message;
    private String author;
    private String photoUrl;
    private String timeStamp;

    public Message(){}

    @Override
    public boolean equals(@Nullable Object obj) {
        Message msg=(Message) obj;

        return super.equals(obj);
    }

    public Message(String message, String author, String photoUrl, String timeStamp) {
        this.message = message;
        this.author = author;
        this.photoUrl = photoUrl;
        this.timeStamp = timeStamp;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
