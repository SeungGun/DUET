package com.example.duet.model;

import java.util.Date;

public class ReplyData {
    private String replyID;
    private String postIDtoReply;
    private User writer;
    private String body;
    private Date replyDate;
    private boolean isSelected;
    private boolean isHidden;

    public ReplyData() {

    }

    public ReplyData(String replyID, String postIDtoReply, User writer, String body, Date replyDate, boolean isSelected, boolean isHidden) {
        this.replyID = replyID;
        this.postIDtoReply = postIDtoReply;
        this.writer = writer;
        this.body = body;
        this.replyDate = replyDate;
        this.isSelected = isSelected;
        this.isHidden = isHidden;
    }

    public ReplyData(String postIDtoReply, User writer, String body) {
        this.postIDtoReply = postIDtoReply;
        this.writer = writer;
        this.body = body;
        this.replyDate = new Date();
        this.isSelected = false;
        this.isHidden = false;
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

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }
}
