package com.example.duet.cardview;

import java.util.ArrayList;

public class CardData {
    private String title;
    private String participate;
    private ArrayList<String> group;
    private String mentor;

    public CardData(String title, String mentor, ArrayList<String> groups){
        this.title = title;
        this.mentor = mentor;
        this.group = groups;
    }

    public CardData(){}



    public String getMentor() {
        return mentor;
    }

    public String getParticipate() {
        return participate;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getGroup() { return group; }

}
