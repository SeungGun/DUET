package com.example.duet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.duet.cardview.BulletData;
import com.example.duet.cardview.MessageAdapter;
import com.example.duet.cardview.MessageData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChattingRoomActivity extends AppCompatActivity {
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    ArrayList<MessageData> messageDataList;
    MessageAdapter messageAdapter;
    String uid;
    String convId;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room);

        Intent getIntent = getIntent();
        uid = getIntent.getStringExtra("uid");
        convId = getIntent.getStringExtra("conv_id");


        EditText mMessageEditText = (EditText)findViewById(R.id.messageEditText);
        ImageButton mSendButton = (ImageButton)findViewById(R.id.sendButton);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.messageRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);



        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MessageData message = new MessageData(mMessageEditText.getText().toString(), uid, null);
                mRef.child("messages/" + convId).push().setValue(message);
                // Clear input box
                mMessageEditText.setText("");
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        recvMessage();

    }

    private void recvMessage() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    MessageData messageData = snapshot.getValue(MessageData.class);
                    messageAdapter.addItem(messageData);
                    messageAdapter.notifyDataSetChanged();
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
            mRef.child("messages/"+convId).addChildEventListener(mChildEventListener);
        }
    }
}