package com.example.myapplication.account.course;

public class CourseModel {
    String courseName;
    String batchName;
    String courseId;
    String batchId;
    String courseCode;
    String teacherId;
    String teacherName;
    String securityKey;
    String imageUrl;

    public CourseModel() {
    }

    public CourseModel(String courseName, String batchName, String courseCode, String courseId, String batchId, String teacherId, String teacherName, String securityKey) {
        this.courseName = courseName;
        this.batchName = batchName;
        this.courseId = courseId;
        this.batchId = batchId;
        this.courseCode = courseCode;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.securityKey = securityKey;

    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
