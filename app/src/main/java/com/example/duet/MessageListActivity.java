package com.example.duet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.duet.R;
import com.example.duet.adapter.CardAdapter;
import com.example.duet.model.CardData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageListActivity extends AppCompatActivity {
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    ChildEventListener mChildEventListener;
    CardAdapter mAdapter;
    ArrayList<CardData> cardDataArrayList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        ListView listView = (ListView) findViewById(R.id.listView);

        cardDataArrayList = new ArrayList<>();
        mAdapter = new CardAdapter(this, cardDataArrayList);
        listView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        recvChatRoom();

    }

    private void recvChatRoom() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    Map<String, Boolean> member = new HashMap<String, Boolean>();
                    Boolean AmIIn = false;

                    for (DataSnapshot ds: snapshot.getChildren()) {


                        if (ds.getKey().equals("members")) {
                            for (DataSnapshot dsMember: ds.getChildren()) {
                                if(dsMember.getKey().equals(mAuth.getUid())) {
                                    AmIIn = true;
                                }
                                member.put(dsMember.getKey(), dsMember.getValue(Boolean.class));
                            }

                            Log.d("user", member.toString());
                        }
                    }

                    if (AmIIn) {
                        CardData cd = snapshot.getValue(CardData.class);
                        cd.setMembers(member);
                        cd.setConvKey(snapshot.getKey());
                        cardDataArrayList.add(cd);
                        mAdapter.notifyDataSetChanged();
                    }


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
            mRef.child("chat_meta").addChildEventListener(mChildEventListener);

        }

    }

}