package com.example.duet.model;

public class PostData {
    private String postID;
    private String writerID;
    private String title;
    private String[] category;
    private String writeDate;
    private String body;
    private int likeCount;
    private int allocPoint;
    private boolean isAllowReply;

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

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
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

    public boolean isAllowReply() {
        return isAllowReply;
    }

    public void setAllowReply(boolean allowReply) {
        isAllowReply = allowReply;
    }
}
