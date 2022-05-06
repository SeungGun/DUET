package com.example.duet.util;

import com.example.duet.cardview.CardData;
import com.example.duet.cardview.CardAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * @auther Me
 * @since 2022/05/03 10:45 오후
 Just implementation
 Planned to use later
 **/

public class FirebaseRDB {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private CardAdapter mAdpater;
    public CardData cdInfo;


    public FirebaseRDB() {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

    }

    public void setCdInfo(CardData cdInfo) {
        this.cdInfo = cdInfo;
    }

    public CardData getCdInfo() {
        return cdInfo;
    }

    public void readData(ArrayList<CardData> cardDataList){

    }
}
