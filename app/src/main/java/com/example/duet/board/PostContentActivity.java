package com.example.duet.board;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostContentActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private TextView postIDTextView;
    private TextView writerNicknameTextView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView bodyTextView;
    private PostData data;
    private LinearLayout replyContainer;
    private EditText inputReply;
    private Button submitReplyButton;
    private RecyclerView replyRecyclerView;
    private ArrayList<ReplyData> replyDataArrayList;
    private TestReplyAdapter adapter;
    private int checkSum = 0;
    private int arrSize = 0;
    private Bundle bundle;
    private Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg){
            checkSum += msg.getData().getInt("count");
            if(arrSize == checkSum){
                adapter = new TestReplyAdapter(replyDataArrayList, getApplicationContext());
                replyRecyclerView.setAdapter(adapter);
                return;
            }

            if(msg.getData().getBoolean("add_reply")) {
                Firestore.getAllReplyOnPostForOwner(data.getPostID()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            replyDataArrayList.clear();
                            int i=0;
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                replyDataArrayList.add(documentSnapshot.toObject(ReplyData.class));
                                if (replyDataArrayList.get(i).isWaiting()) {
                                    replyDataArrayList.get(i).setViewType(1);
                                }
                                int finalI = i;
                                Firestore.getUserData(replyDataArrayList.get(i).getWriter().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            replyDataArrayList.get(finalI).setWriter(task.getResult().toObject(User.class));
                                            bundle.putInt("count", 1);
                                            Message msg = handler.obtainMessage();
                                            msg.setData(bundle);
                                            handler.sendMessage(msg); // 메세지 전달
                                        } else {

                                        }
                                    }
                                });
                                i++;
                            }
//                            adapter = new TestReplyAdapter(replyDataArrayList, getApplicationContext());
//                            replyRecyclerView.setAdapter(adapter);
                        }
                    }
                });
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);
        Intent intent = getIntent();
        data = (PostData) intent.getSerializableExtra("data");
        linearLayout = findViewById(R.id.content_container);
        postIDTextView = findViewById(R.id.content_post_id);
        writerNicknameTextView = findViewById(R.id.content_writer_id);
        titleTextView = findViewById(R.id.content_title);
        dateTextView = findViewById(R.id.content_write_date);
        bodyTextView = findViewById(R.id.content_body);
        replyContainer = findViewById(R.id.reply_container);
        replyRecyclerView = findViewById(R.id.reply_recycler_view);
        replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        replyDataArrayList = new ArrayList<>();
        bundle = new Bundle();

        if(User.currentUser.getUid().equals(data.getWriter().getUid())) {
            Firestore.getAllReplyOnPostForOwner(data.getPostID()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        replyDataArrayList.clear();
                        int i=0;
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            int finalI = i;
                            replyDataArrayList.add(documentSnapshot.toObject(ReplyData.class));
                            Firestore.getUserData(replyDataArrayList.get(finalI).getWriter().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        replyDataArrayList.get(finalI).setWriter(task.getResult().toObject(User.class));

                                        bundle.putInt("count", 1);
                                        Message msg = handler.obtainMessage();
                                        msg.setData(bundle);
                                        handler.sendMessage(msg); // 메세지 전달
                                    }
                                    else{

                                    }
                                }
                            });
                            i++;
                        }
                        arrSize = replyDataArrayList.size();
//                        adapter = new TestReplyAdapter(replyDataArrayList, getApplicationContext());
//                        replyRecyclerView.setAdapter(adapter);
                    }
                }
            });
        }
        else{
            Firestore.getAllReplyOnPostForAnybody(data.getPostID()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        replyDataArrayList.clear();
                        for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                            replyDataArrayList.add(documentSnapshot.toObject(ReplyData.class));
                        }
                        adapter = new TestReplyAdapter(replyDataArrayList, getApplicationContext());
                        replyRecyclerView.setAdapter(adapter);
                    }
                }
            });
        }
        int state = data.getStateAllowReply();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (state == 0) {
            View view = inflater.inflate(R.layout.reply_allow_layout, replyContainer);
            inputReply = view.findViewById(R.id.input_reply);
            submitReplyButton = view.findViewById(R.id.submit_button);
            submitReplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReplyData newData = new ReplyData(data.getPostID()
                            , data.getWriter().getUid()
                            , User.currentUser
                            , inputReply.getText().toString()
                            , false, 0);
                    replyDataArrayList.add(newData);
                    Firestore.addReplyData(newData)
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
                                                            bundle.putBoolean("add_reply", true);
                                                            Message msg = handler.obtainMessage();
                                                            msg.setData(bundle);
                                                            handler.sendMessage(msg);
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
            /*
                AlertDialog 하기 → 수락을 해야 댓글이 보인다는 메세지
             */
            View view = inflater.inflate(R.layout.reply_allow_layout, replyContainer);
            inputReply = view.findViewById(R.id.input_reply);
            submitReplyButton = view.findViewById(R.id.submit_button);
            submitReplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReplyData newData = new ReplyData(data.getPostID()
                            , data.getWriter().getUid()
                            , User.currentUser
                            , inputReply.getText().toString()
                            , true, 1);
                    replyDataArrayList.add(newData);
                    Firestore.addReplyData(newData)
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
        writerNicknameTextView.setText(data.getWriter().getNickname());
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

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.d("클릭", "클릭 "+item.getGroupId());
        Log.d("id",replyDataArrayList.get(item.getGroupId()).getPostWriterID());
        switch (item.getItemId()){
            case R.id.action_adopt:
                if(User.currentUser.getUid().equals(replyDataArrayList.get(item.getGroupId()).getPostWriterID())
                        && !User.currentUser.getUid().equals(replyDataArrayList.get(item.getGroupId()).getWriter().getUid())){
                    Log.d("메뉴 클릭", "채택");
                    Firestore.updateReplySelection(replyDataArrayList.get(item.getGroupId()).getReplyID())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PostContentActivity.this, "채택 성공", Toast.LENGTH_SHORT).show();
                                ReplyData current = replyDataArrayList.get(item.getGroupId());
                                current.setSelected(true);
                                adapter.setItem(item.getGroupId(), current);
                                adapter.notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(PostContentActivity.this, "채택 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Log.d("메뉴 클릭 ", "소유자 아니거나 본인글에 대한 본인 댓글");
                }
                return true;
            case R.id.action_delete:
                if(User.currentUser.getUid().equals(replyDataArrayList.get(item.getGroupId()).getPostWriterID())
                        || User.currentUser.getUid().equals(replyDataArrayList.get(item.getGroupId()).getWriter().getUid())){
                    Log.d("메뉴 클릭", "삭제");
                    Firestore.removeReplyOnPostByOwner(replyDataArrayList.get(item.getGroupId()).getReplyID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PostContentActivity.this, "삭제 성공", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    adapter.removeItem(item.getGroupId());
                }
                else{
                    Log.d("메뉴 클릭", "삭제 - 소유자 아니거나 댓글 작성자 아님");
                }
                return true;
            case R.id.action_report:
                if(!User.currentUser.getUid().equals(replyDataArrayList.get(item.getGroupId()).getWriter().getUid())){
                    Firestore.updateUserReliability(false, replyDataArrayList.get(item.getGroupId()).getWriter().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PostContentActivity.this, "신고 성공(신뢰도 차감)", Toast.LENGTH_SHORT).show();

                                checkSum = 0;
                                Firestore.getAllReplyOnPostForOwner(data.getPostID()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            replyDataArrayList.clear();
                                            int i=0;
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                replyDataArrayList.add(documentSnapshot.toObject(ReplyData.class));
                                                if (replyDataArrayList.get(i).isWaiting()) {
                                                    replyDataArrayList.get(i).setViewType(1);
                                                }
                                                int finalI = i;
                                                Firestore.getUserData(replyDataArrayList.get(i).getWriter().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            replyDataArrayList.get(finalI).setWriter(task.getResult().toObject(User.class));
                                                            bundle.putInt("count", 1);
                                                            Message msg = handler.obtainMessage();
                                                            msg.setData(bundle);
                                                            handler.sendMessage(msg); // 메세지 전달
                                                        } else {

                                                        }
                                                    }
                                                });
                                                i++;
                                            }
                                            arrSize = replyDataArrayList.size();
//                            adapter = new TestReplyAdapter(replyDataArrayList, getApplicationContext());
//                            replyRecyclerView.setAdapter(adapter);
                                        }
                                    }
                                });
                            }
                            else{
                                task.getException().printStackTrace();
                                Toast.makeText(PostContentActivity.this, "신고 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Log.d("메뉴 클릭", "신고");
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}