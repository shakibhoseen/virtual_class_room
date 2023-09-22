package com.example.myapplication.account.assignment;

public class AssignmentSubmitModel {
    String snapKey;
    String username;
    String imageUrl;
    String roll;
    private String fileName;
    private String fileUrl;
   private String id;
    long publish;

    public AssignmentSubmitModel() {
    }

    public AssignmentSubmitModel(String fileName, String fileUrl, String id, long publish) {

        this.fileName = fileName;
        this.fileUrl = fileUrl;

        this.id = id;
        this.publish = publish;
    }

   /* public AssignmentSubmitModel(String username, String imageUrl, String roll, String fileName, String fileUrl, String id, long publish) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.roll = roll;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.id = id;
        this.publish = publish;
    }*/

    public String getSnapKey() {
        return snapKey;
    }

    public void setSnapKey(String snapKey) {
        this.snapKey = snapKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPublish() {
        return publish;
    }

    public void setPublish(long publish) {
        this.publish = publish;
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
}
