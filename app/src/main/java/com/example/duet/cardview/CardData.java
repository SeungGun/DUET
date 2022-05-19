package com.example.duet.cardview;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardData {
    private String title;
    private Map<String, Boolean> members;
    private String lastMessage;
    private String convKey;

    public CardData(String title,  String lastMessage){
        this.title = title;
        this.lastMessage = lastMessage;
    }


    public CardData(){}

    //public ArrayList<String> getMembers() {return members; }

    public String getTitle() {
        return title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    public void setConvKey(String convKey) {
        this.convKey = convKey;
    }

    public String getConvKey() {
        return convKey;
    }

    public String getMembers() {
        return members.keySet().toString();
    }
}
