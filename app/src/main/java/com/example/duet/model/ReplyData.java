package com.example.duet.model;

import java.util.Date;

public class ReplyData {
    private String replyID;
    private String postIDtoReply;
    private String postWriterID;
    private User writer;
    private String body;
    private Date replyDate;
    private boolean isSelected;
    private boolean isWaiting;
    private int viewType;

    public ReplyData() {

    }

    public ReplyData(String replyID, String postIDtoReply, String postWriterID, User writer, String body, Date replyDate, boolean isSelected, boolean isWaiting) {
        this.replyID = replyID;
        this.postIDtoReply = postIDtoReply;
        this.postWriterID = postWriterID;
        this.writer = writer;
        this.body = body;
        this.replyDate = replyDate;
        this.isSelected = isSelected;
        this.isWaiting = isWaiting;
        this.viewType = 0;
    }

    public ReplyData(String postIDtoReply, String postWriterID, User writer, String body, boolean isWaiting, int viewType) {
        this.postIDtoReply = postIDtoReply;
        this.postWriterID = postWriterID;
        this.writer = writer;
        this.body = body;
        this.replyDate = new Date();
        this.isSelected = false;
        this.isWaiting = isWaiting;
        this.viewType = viewType;
    }

    public String getReplyID() {
        return replyID;
    }

    public void setReplyID(String replyID) {
        this.replyID = replyID;
    }

    public String getPostIDtoReply() {
        return postIDtoReply;
    }

    public void setPostIDtoReply(String postIDtoReply) {
        this.postIDtoReply = postIDtoReply;
    }

    public User getWriter() {
        return writer;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getPostWriterID() {
        return postWriterID;
    }

    public void setPostWriterID(String postWriterID) {
        this.postWriterID = postWriterID;
    }
}
