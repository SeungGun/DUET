package com.example.duet.myPost;

public class MyPostItem {
    private String titleStr ;
    private String descStr ;

    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }

    public String getTitle() { return titleStr; }

    public String getDesc() {
        return this.descStr ;
    }
}
