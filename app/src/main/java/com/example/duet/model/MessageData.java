package com.example.duet.model;

public class MessageData {
    private String text;
    private String name;
    private String photoUrl;
    private int viewType;

    public MessageData() {
    }

    public MessageData(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public MessageData(String text, String name, String photoUrl, int viewType) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
