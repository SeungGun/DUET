package com.example.duet.activity.testbed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.duet.R;
import com.example.duet.cardview.CardData;
import com.example.duet.cardview.MyAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * @auther Me
 * @since 2022/05/03 10:49 오후
 Loading Group study room if the user logged in & has previous data
 **/

public class CardActivity extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference mRef;
    ArrayList<CardData> cardDataArrayList = new ArrayList<CardData>();
    MyAdapter mAdapter;
    String uid;
    ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ListView listView = (ListView) findViewById(R.id.listView);
        Button admin = (Button) findViewById(R.id.adminBtn);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                intent.putExtra("uid", user.getUid());
                startActivity(intent);
            }
        });

        mAdapter = new MyAdapter(this, cardDataArrayList);
        listView.setAdapter(mAdapter);

        if (user != null) {
            mRef = FirebaseDatabase.getInstance().getReference();
            uid = user.getUid();
        } else {
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(intent);
        }

    }

    /**
     * @auther Me
     * @since 2022/05/03 10:44 오후
     let's Assume that user already logged in
     **/

    @Override
    protected void onResume() {
        super.onResume();
        recvChat();

    }

    /**
     * @auther Me
     * @since 2022/05/03 10:42
     Attach firebase child listener under classes/"uid"
     Update when value under classes/"uid" changed
     **/

    private void recvChat() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    CardData cardData = snapshot.getValue(CardData.class);
                    cardDataArrayList.add(cardData);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            };
            mRef.child("classes").child(uid).addChildEventListener(mChildEventListener);
        }
    }
}