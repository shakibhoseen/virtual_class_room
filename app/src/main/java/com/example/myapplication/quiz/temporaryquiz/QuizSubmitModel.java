package com.example.myapplication.quiz.temporaryquiz;

public class QuizSubmitModel {
   private String id;
   private long publish;
   private double result;
   private String username;
   private String imageUrl;
   private String roll;

    public QuizSubmitModel() {
    }

    public QuizSubmitModel( String id, long publish ,double result) {

        this.result = result;
        this.id = id;
        this.publish = publish;
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

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
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
