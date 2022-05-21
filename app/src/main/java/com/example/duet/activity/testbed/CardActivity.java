package com.example.duet.activity.testbed;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.duet.R;
import com.example.duet.cardview.BulletAdapter;
import com.example.duet.cardview.BulletData;
import com.example.duet.cardview.CardData;
import com.example.duet.cardview.CardAdapter;
import com.example.duet.util.CustomProgressDialog;
import com.example.duet.util.RealTimeDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

/**
 * @auther Me
 * @since 2022/05/03 10:49 오후
 Loading Group study room if the user logged in & has previous data
 **/

public class CardActivity extends AppCompatActivity {

    FirebaseUser user;
    DatabaseReference mRef;
    ArrayList<BulletData> bulletDataArrayList = new ArrayList<BulletData>();
    BulletAdapter mAdapter;
    String uid;
    ChildEventListener mChildEventListener;
    CustomProgressDialog dialog;

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        mRef = RealTimeDatabase.getDatabaseRef();
                    }
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ListView listView = (ListView) findViewById(R.id.listView);
        Button admin = (Button) findViewById(R.id.adminBtn);
        Button chatBtn = (Button)findViewById(R.id.chatRoomBtn);

        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
        firebaseMessaging.subscribeToTopic("message_notification");

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                intent.putExtra("uid", user.getUid());
                startActivity(intent);
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessageListActivity.class);
                startActivity(intent);
            }
        });


        mAdapter = new BulletAdapter(this, bulletDataArrayList);
        listView.setAdapter(mAdapter);

        if (user != null) {
            mRef = RealTimeDatabase.getDatabaseRef();
            uid = user.getUid();
        } else {
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivityResult.launch(intent);
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
        if (user != null)
            recvBullet();


    }

    /**
     * @auther Me
     * @since 2022/05/03 10:42
     Attach firebase child listener under classes/"uid"
     Update when value under classes/"uid" changed
     **/

    private void recvBullet() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    BulletData bulletData = snapshot.getValue(BulletData.class);
                    bulletDataArrayList.add(bulletData);
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
            mRef.child("bulletin").addChildEventListener(mChildEventListener);

        }

    }


}