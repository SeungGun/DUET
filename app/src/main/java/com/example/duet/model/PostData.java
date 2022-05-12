package com.example.duet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class PostData implements Serializable {
    private String postID;
    private User writer;
    private String title;
    private ArrayList<String> category;
    private Date writeDate;
    private String body;
    private int likeCount;
    private int allocPoint;
    private int stateAllowReply;
    private ArrayList<String> postImageUrls;

    public PostData() {

    }

    public PostData(User writer, String title, ArrayList<String> category, String body, int allocPoint, int stateAllowReply, ArrayList<String> postImageUrls) {
        this.writer = writer;
        this.title = title;
        this.category = null; // 임시 처리, 카테고리 데이터 만들면 변경하기
        this.body = body;
        this.likeCount = 0;
        this.allocPoint = allocPoint;
        this.stateAllowReply = stateAllowReply;
        this.postImageUrls = postImageUrls;
        this.writeDate = new Date();
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

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
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

    public Date getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(Date writeDate) {
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

    @Override
    public String toString() {
        return "PostData{" +
                "postID='" + postID + '\'' +
                ", writerID='" + writer.getUid() + '\'' +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", writeDate=" + writeDate +
                ", body='" + body + '\'' +
                ", likeCount=" + likeCount +
                ", allocPoint=" + allocPoint +
                ", stateAllowReply=" + stateAllowReply +
                ", postImageUrls=" + postImageUrls +
                '}';
    }
}
