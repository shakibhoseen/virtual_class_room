package com.example.myapplication.group_chat;

import java.util.List;

public class Chat {
    String sender;
    String contentId;
    String senderNm;
    String senderImg;
    String receiver;
    String message;
    String imageUrl;
    String totalSeen;
    long publish;
    boolean clickOne;
    List<SeenList> seenlist;
    public Chat() {
    }

    public Chat(String sender, String receiver, String message , String imageUrl, List<SeenList> seenlist, String totalSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.imageUrl = imageUrl;
        this.seenlist = seenlist;
        this.totalSeen = totalSeen;
    }

    public String getTotalSeen() {
        return totalSeen;
    }

    public void setTotalSeen(String totalSeen) {
        this.totalSeen = totalSeen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<SeenList> getSeenlist() {
        return seenlist;
    }

    public void setSeenlist(List<SeenList> seenlist) {
        this.seenlist = seenlist;
    }

    public long getPublish() {
        return publish;
    }

    public void setPublish(long publish) {
        this.publish = publish;
    }

    public String getSenderNm() {
        return senderNm;
    }

    public void setSenderNm(String senderNm) {
        this.senderNm = senderNm;
    }

    public String getSenderImg() {
        return senderImg;
    }

    public void setSenderImg(String senderImg) {
        this.senderImg = senderImg;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public boolean isClickOne() {
        return clickOne;
    }

    public void setClickOne(boolean clickOne) {
        this.clickOne = clickOne;
    }
}
