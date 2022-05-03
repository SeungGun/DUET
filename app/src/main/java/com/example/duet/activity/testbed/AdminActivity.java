package com.example.duet.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.duet.R;
import com.example.duet.cardview.CardData;
import com.example.duet.util.CreateText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout);



        Intent flyingIntent = getIntent();
        Button btn = (Button) findViewById(R.id.dummyBtn);

        uid = flyingIntent.getStringExtra("uid");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateText createText = new CreateText();

                writeNewThing("Who Am I", "Won Kim", createText.generateRandomChunk());
            }
        });


    }



    private void writeNewThing(String title, String mentor, ArrayList<String> group) {
        CardData cardData = new CardData(title, mentor, group);

        mDatabase.child("classes").child(uid).push().setValue(cardData);

    }
}