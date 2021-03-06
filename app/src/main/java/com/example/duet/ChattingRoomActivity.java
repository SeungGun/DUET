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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.duet.adapter.MessageAdapter;
import com.example.duet.model.MessageData;
import com.example.duet.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChattingRoomActivity extends AppCompatActivity {
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    ArrayList<MessageData> messageDataList;
    MessageAdapter messageAdapter;
    String uid;
    String convId;
    String mTitle;
    String mMembers;
    String username;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    ChildEventListener mChildEventListener;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room);
        Intent getIntent = getIntent();
        uid = getIntent.getStringExtra("uid");
        convId = getIntent.getStringExtra("conv_id");
        mMembers = getIntent.getStringExtra("members");
        mTitle = getIntent.getStringExtra("title");
        username = User.currentUser.getUserName();

        ImageButton backBtn = (ImageButton)findViewById(R.id.backBtn);
        TextView title = (TextView)findViewById(R.id.chatTitle);

        title.setText(mTitle);

        EditText mMessageEditText = (EditText)findViewById(R.id.messageEditText);
        ImageButton mSendButton = (ImageButton)findViewById(R.id.sendButton);
        recyclerView = (RecyclerView)findViewById(R.id.messageRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);


        /**
         * @auther Me
         * @since 2022/05/12 12:02 ??????
         ?????? EditText??? ???????????? ?????? sendbutton??? ????????????
         **/
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

        //?????? ????????? ?????? ??? ?????? ?????? ???????????? ????????????
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MessageData message = new MessageData(mMessageEditText.getText().toString(), User.currentUser.getUserName(), null);

                //TODO ?????? ????????? ??? data
                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                //Notification??? ?????? ????????? ????????? ??????
                Map<String, Object> update = new HashMap<>();


                //update.put("lastSender", username);
                update.put("lastMessage", username + ": " + mMessageEditText.getText().toString());

                mRef.child("messages/" + convId).push().setValue(message);
                mRef.child("chat_meta/" + convId).updateChildren(update);
                // Clear input box
                mMessageEditText.setText("");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

                    if (messageData.getName().equals(User.currentUser.getUserName())){
                        messageData.setViewType(1);
                    }
                    else {
                        messageData.setViewType(0);
                    }
                    messageAdapter.addItem(messageData);
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

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