package com.example.myapplication.account.teacher;

public class TeacherModel {
    String id;
    String imageUrl;
    String name;
    String phone;
    String status;
    String email;

    public TeacherModel() {
    }

    public TeacherModel(String id, String imageUrl, String name, String phone, String status, String email) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.phone = phone;
        this.status = status;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
