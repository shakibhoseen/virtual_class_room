package com.example.myapplication.quiz;

public class QuizDetailsModel {
    private String title;
    private String description;
    private long duration;
    private String totalQsn;
   private long strtTime;
   private long endTime;
    private String prepare;
   private String dataKey;
    private long publish;
   private double increase = 1;
   private double decrease;
    private String submitCk;
    private int countStudent;


    public String getSubmitCk() {
        return submitCk;
    }

    public void setSubmitCk(String submitCk) {
        this.submitCk = submitCk;
    }

    public int getCountStudent() {
        return countStudent;
    }

    public void setCountStudent(int countStudent) {
        this.countStudent = countStudent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTotalQsn() {
        return totalQsn;
    }

    public void setTotalQsn(String totalQsn) {
        this.totalQsn = totalQsn;
    }

    public long getStrtTime() {
        return strtTime;
    }

    public void setStrtTime(long strtTime) {
        this.strtTime = strtTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getPrepare() {
        return prepare;
    }

    public void setPrepare(String prepare) {
        this.prepare = prepare;
    }

    public long getPublish() {
        return publish;
    }

    public void setPublish(long publish) {
        this.publish = publish;
    }

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public double getIncrease() {
        return increase;
    }

    public void setIncrease(double increase) {
        this.increase = increase;
    }

    public double getDecrease() {
        return decrease;
    }

    public void setDecrease(double decrease) {
        this.decrease = decrease;
    }
}
