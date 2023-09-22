package com.example.myapplication.account.teacher;

public class SecureModel {
    String teacher;
    String student;
    String admin;

    public SecureModel() {
    }

    public SecureModel(String teacher, String student, String admin) {
        this.teacher = teacher;
        this.student = student;
        this.admin = admin;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
