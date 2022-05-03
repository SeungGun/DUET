package com.example.duet.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.duet.cardview.CardData;
import com.example.duet.cardview.MyAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseRDB {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private MyAdapter mAdpater;
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
        CardData card;
        mRef.child("classes").child("admin").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    cardDataList.add(task.getResult().getValue(CardData.class));

                }
            }
        });


    }
}
