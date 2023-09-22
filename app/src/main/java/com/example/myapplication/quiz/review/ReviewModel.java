package com.example.myapplication.quiz.review;

import java.io.Serializable;

public class ReviewModel implements Serializable {
    String questionNo;
    String correctAns;
    String userAns;
    long publish;
    double result;
    double pastResult;

    public ReviewModel() {
    }

    public ReviewModel(String questionNo, String correctAns, String userAns, long publish, double result) {
        this.questionNo = questionNo;
        this.correctAns = correctAns;
        this.userAns = userAns;
        this.publish = publish;
        this.result = result;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }

    public String getUserAns() {
        return userAns;
    }

    public void setUserAns(String userAns) {
        this.userAns = userAns;
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

    public double getPastResult() {
        return pastResult;
    }

    public void setPastResult(double pastResult) {
        this.pastResult = pastResult;
    }
}
