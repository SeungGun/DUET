package com.example.duet.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class PostData {
    private String postID;
    private String writerID;
    private String title;
    private ArrayList<String> category;
    private Timestamp writeDate;
    private String body;
    private int likeCount;
    private int allocPoint;
    private int stateAllowReply;
    private ArrayList<String> postImageUrls;

    public PostData() {

    }

    public PostData(String writerID, String title, ArrayList<String> category, String body, int allocPoint, ArrayList<String> postImageUrls) {
        this.writerID = writerID;
        this.title = title;
        this.category = category;
        this.body = body;
        this.likeCount = 0;
        this.allocPoint = allocPoint;
        this.stateAllowReply = 1;
        this.postImageUrls = postImageUrls;
        this.writeDate = new Timestamp(new Date().getTime());
    }

    public ArrayList<String> getPostImageUrls() {
        return postImageUrls;
    }

    public void setPostImageUrls(ArrayList<String> postImageUrls) {
        this.postImageUrls = postImageUrls;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getWriterID() {
        return writerID;
    }

    public void setWriterID(String writerID) {
        this.writerID = writerID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public Timestamp getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(Timestamp writeDate) {
        this.writeDate = writeDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getAllocPoint() {
        return allocPoint;
    }

    public void setAllocPoint(int allocPoint) {
        this.allocPoint = allocPoint;
    }

    public int getStateAllowReply() {
        return stateAllowReply;
    }

    public void setStateAllowReply(int stateAllowReply) {
        this.stateAllowReply = stateAllowReply;
    }
}
