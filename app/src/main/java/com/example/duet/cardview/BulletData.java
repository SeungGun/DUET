package com.example.duet.cardview;

import java.util.ArrayList;

//TODO Add timestamp

public class BulletData {
    private String title;
    private String content;
    private String conv_key;

    public BulletData(String title, String content, String conv_key){
        this.title = title;
        this.content = content;
        this.conv_key = conv_key;
    }

    public BulletData(){}


    public String getTitle() {
        return "TiTlE: "+title;
    }

    public String getContent() {
        return "CoNtEnT: " +content;
    }

    public String getConv_key() {
        return conv_key;
    }
}
