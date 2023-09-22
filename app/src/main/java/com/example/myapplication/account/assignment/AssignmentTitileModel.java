package com.example.myapplication.account.assignment;

public class AssignmentTitileModel {
    private String title;
    private String description;
    private long strtTime;
    private long endTime;
    private String prepare;
    private String dataKey;
    private long publish;
    private String submitCk;
    private int countStudent;

    public int getCountStudent() {
        return countStudent;
    }

    public void setCountStudent(int countStudent) {
        this.countStudent = countStudent;
    }

    public String getSubmitCk() {
        return submitCk;
    }

    public void setSubmitCk(String submitCk) {
        this.submitCk = submitCk;
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

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public long getPublish() {
        return publish;
    }

    public void setPublish(long publish) {
        this.publish = publish;
    }
}
