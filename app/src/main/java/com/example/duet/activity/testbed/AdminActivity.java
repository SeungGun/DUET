package com.example.duet.activity.testbed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.duet.R;
import com.example.duet.cardview.BulletData;
import com.example.duet.util.RealTimeDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private static final String DEFAULT = "DEFAULT";
    private DatabaseReference mDatabase;
    private String uid;
    Button btnPosting;
    EditText title;
    EditText content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout);



        Intent flyingIntent = getIntent();
        Button btn = (Button) findViewById(R.id.dummyBtn);
        btnPosting = (Button) findViewById(R.id.postingBtn);
        title = (EditText)findViewById(R.id.editTitle);
        content = (EditText)findViewById(R.id.editContent);

        uid = flyingIntent.getStringExtra("uid");
        mDatabase = RealTimeDatabase.getDatabaseRef();



        btnPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                writeBulletData();

            }
        });


    }


    /**
     * @auther Me
     * @since 2022/05/06 10:37 오전
     Write bulletin
     Use bulletin board Id as a conversation Id
     Chat channel will be implemented based on each conversation Id
     **/

    private void writeBulletData() {
        Map<String, Object> update = new HashMap<>();
        DatabaseReference mRef = mDatabase.child("bulletin").push();

        String key = mRef.getKey();

        //TODO content, title null checking
        //TODO Button invisible to Bulletin board owner

        String sendContent = content.getText().toString();
        String sendTitle = title.getText().toString();
        String sendWriterId = uid;
        mRef.setValue(new BulletData(sendTitle, sendContent, key));

        update.put(uid, true);
        mDatabase.child("members"+"/"+key).setValue(update);

        update.clear();
        update.put("title", sendTitle);
        mDatabase.child("chat_meta"+"/"+key).setValue(update);
        update.clear();
        update.put(uid, true);
        mDatabase.child("chat_meta"+ "/"+key +"/" + "members").setValue(update);
        update.clear();
        update.put("conv_key", key);
        mDatabase.child("user_in"+ "/"+uid).push().setValue(update);


    }




}