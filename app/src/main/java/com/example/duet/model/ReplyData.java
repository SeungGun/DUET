package com.example.duet.model;

public class ReplyData {
    private String replyID;
    private String postIDtoReply;
    private String writerID;
    private String body;
    private String replyDate;
    private boolean isSelected;
    private boolean isHidden;

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

    public String getWriterID() {
        return writerID;
    }

    public void setWriterID(String writerID) {
        this.writerID = writerID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
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
