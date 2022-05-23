package com.example.duet.activity.testbed;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.duet.R;
import com.example.duet.cardview.BulletAdapter;
import com.example.duet.cardview.BulletData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    BulletAdapter bulletAdapter;
    ArrayList<BulletData> bulletDataList;
    ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ListView listView = (ListView) findViewById(R.id.bulletinList);
        bulletDataList = new ArrayList<BulletData>();
        bulletAdapter = new BulletAdapter(this, bulletDataList);
        listView.setAdapter(bulletAdapter);



    }

    /**
     * @auther Me
     * @since 2022/05/03 10:44 오후
    let's Assume that user already logged in
     **/

    @Override
    protected void onResume() {
        super.onResume();
        recvBullet();

    }

    /**
     * @auther Me
     * @since 2022/05/03 10:42
    Attach firebase child listener under bulletin/
    Update when value under bulletin changed
     **/

    private void recvBullet() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    BulletData bulletData = snapshot.getValue(BulletData.class);
                    bulletDataList.add(bulletData);
                    bulletAdapter.notifyDataSetChanged();
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
            mDatabase.child("bulletin").addChildEventListener(mChildEventListener);
        }
    }
}