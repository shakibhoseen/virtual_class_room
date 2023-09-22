package com.example.myapplication.account;

public class StudentModel {
    String username;
    String imageUrl;
    String roll;
    String id;
    String status;
    String batch;
    String email;

    public StudentModel() {
    }

    public StudentModel(String username, String imageUrl, String roll, String id, String status, String batch, String email) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.roll = roll;
        this.id = id;
        this.status = status;
        this.batch = batch;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
