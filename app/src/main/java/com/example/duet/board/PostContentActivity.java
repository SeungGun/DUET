package com.example.duet.board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.duet.R;
import com.example.duet.adapter.TestReplyAdapter;
import com.example.duet.model.PostData;
import com.example.duet.model.ReplyData;
import com.example.duet.model.User;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostContentActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private TextView postIDTextView;
    private TextView writerIDTextView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView bodyTextView;
    private PostData data;
    private LinearLayout replyContainer;
    private EditText inputReply;
    private Button submitReplyButton;
    private RecyclerView replyRecyclerView;
    private ArrayList<ReplyData> replyDataArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);
        Intent intent = getIntent();
        data = (PostData) intent.getSerializableExtra("data");
        linearLayout = findViewById(R.id.content_container);
        postIDTextView = findViewById(R.id.content_post_id);
        writerIDTextView = findViewById(R.id.content_writer_id);
        titleTextView = findViewById(R.id.content_title);
        dateTextView = findViewById(R.id.content_write_date);
        bodyTextView = findViewById(R.id.content_body);
        replyContainer = findViewById(R.id.reply_container);
        replyRecyclerView = findViewById(R.id.reply_recycler_view);
        replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        replyDataArrayList = new ArrayList<>();

        Firestore.getAllReplyOnPost(data.getPostID()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        replyDataArrayList.add(documentSnapshot.toObject(ReplyData.class));
                    }
                    TestReplyAdapter adapter = new TestReplyAdapter(replyDataArrayList, getApplicationContext());
                    replyRecyclerView.setAdapter(adapter);
                }
            }
        });
        int state = data.getStateAllowReply();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (state == 0) {
            View view = inflater.inflate(R.layout.reply_allow_layout, replyContainer);
            inputReply = view.findViewById(R.id.input_reply);
            submitReplyButton = view.findViewById(R.id.submit_button);
            submitReplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Firestore.addReplyData(new ReplyData(data.getPostID()
                            , User.currentUser
                            , inputReply.getText().toString()))
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Firestore.insertReplyId(task.getResult().getId())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(PostContentActivity.this, "success", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.e("update reply id in field", "failure");
                                                        }
                                                    }
                                                });
                                    } else {
                                        Log.e("add reply", "failure");
                                    }
                                }
                            });
                }
            });
        } else if (state == 1) {
            View view = inflater.inflate(R.layout.reply_allow_layout, replyContainer);
            inputReply = view.findViewById(R.id.input_reply);
            submitReplyButton = view.findViewById(R.id.submit_button);
            submitReplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Firestore.addReplyData(new ReplyData(data.getPostID()
                            , User.currentUser
                            , inputReply.getText().toString()))
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Firestore.insertReplyId(task.getResult().getId())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(PostContentActivity.this, "success", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.e("update reply id in field", "failure");
                                                        }
                                                    }
                                                });
                                    } else {
                                        Log.e("add reply", "failure");
                                    }
                                }
                            });
                }
            });
        } else {
            inflater.inflate(R.layout.reply_forbidden_layout, replyContainer);
        }

        postIDTextView.setText(data.getPostID());
        writerIDTextView.setText(data.getWriterID());
        titleTextView.setText(data.getTitle());
        dateTextView.setText(data.getWriteDate().toString());
        bodyTextView.setText(data.getBody());

        for (int i = 0; i < data.getPostImageUrls().size(); ++i) {
            ImageView imageView = new ImageView(getApplicationContext());
            linearLayout.addView(imageView);
            Glide.with(getApplicationContext())
                    .load(data.getPostImageUrls().get(i))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) linearLayout.getChildAt(i));
        }
    }
}