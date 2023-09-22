package com.example.myapplication.slide;

public class SlideModel {

    String description;
    String dataKey;   // handle by coding into use snapshot
    long publish;
    String fileName;
    String fileUrl;

    public SlideModel() {
    }

    public SlideModel(String description, long publish, String fileName, String fileUrl) {
        this.description = description;
        this.publish = publish;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPublish() {
        return publish;
    }

    public void setPublish(long publish) {
        this.publish = publish;
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

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }
}
