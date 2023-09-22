package com.example.myapplication.group_chat;

public class SeenList {
    private String name;
    private String ownImg;
    private String userId;

    public SeenList() {
    }

    public SeenList(String name, String ownImg) {
        this.name = name;
        this.ownImg = ownImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnImg() {
        return ownImg;
    }

    public void setOwnImg(String ownImg) {
        this.ownImg = ownImg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
