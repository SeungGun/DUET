package com.example.duet.board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.duet.R;
import com.example.duet.adapter.TestPostDataAdapter;
import com.example.duet.model.PostData;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TestShowPostActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private ArrayList<PostData> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_show_post);
        linearLayout = findViewById(R.id.img_container_test);
        list = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Firestore.getAllPostData().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<String> urls = new ArrayList<>();
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                       PostData postData =  documentSnapshot.toObject(PostData.class);
                       list.add(postData);
                       for(int i=0; i<postData.getPostImageUrls().size(); i++){
                           urls.add(postData.getPostImageUrls().get(i));
                       }
                        Log.d("data", postData.toString());
                    }
                    for(int i=0; i<urls.size(); ++i){
                        ImageView imageView = new ImageView(getApplicationContext());
                        linearLayout.addView(imageView);

                    }
                    TestPostDataAdapter adapter = new TestPostDataAdapter(list, getApplicationContext());
                    adapter.setOnItemClickListener(new TestPostDataAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClicked(int position, PostData data) {
                            Intent intent = new Intent(getApplicationContext(), PostContentActivity.class);
                            intent.putExtra("position", position);
                            intent.putExtra("data", data);
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
                else{
                    Log.d("data", "failure");
                }
            }
        });
    }
}