package com.example.myapplication.account.teacher.ui.share;

import com.example.myapplication.account.StudentModel;

import java.util.List;

public class CombineModel {
    private List<StudentModel> modelList;
    private String batchName;

    public CombineModel() {
    }

    public CombineModel(List<StudentModel> modelList, String batchName) {
        this.modelList = modelList;
        this.batchName = batchName;
    }

    public List<StudentModel> getModelList() {
        return modelList;
    }

    public void setModelList(List<StudentModel> modelList) {
        this.modelList = modelList;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }
}
