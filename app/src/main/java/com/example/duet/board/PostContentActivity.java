package com.example.duet.board;

import androidx.annotation.NonNull;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.duet.R;
import com.example.duet.UserProfileActivity;
import com.example.duet.adapter.ReplyAdapter;
import com.example.duet.model.PostData;
import com.example.duet.model.ReplyData;
import com.example.duet.model.User;
import com.example.duet.util.CustomProgressDialog;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostContentActivity extends AppCompatActivity {

    private LinearLayout imageContainer;
    private ImageView userProfileImage;
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
    private DividerItemDecoration dividerItemDecoration;
    private ReplyAdapter adapter;
    private Button groupJoinBtn;
    private DatabaseReference mRef;
    private CustomProgressDialog customProgressDialog;
    private int checkSum = 0;
    private int arrSize = 0;
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            checkSum += msg.getData().getInt("count");
            if (arrSize == checkSum) {
                adapter = new ReplyAdapter(replyDataArrayList, getApplicationContext(), data.getPostType(), data.getWriter().getUid().equals(User.currentUser.getUid()));
                replyRecyclerView.setAdapter(adapter);
                return;
            }

            if (msg.getData().getBoolean("add_reply")) {
                Firestore.getAllReplyOnPostForOwner(data.getPostID()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            replyDataArrayList.clear();
                            checkSum = 0;
                            int i = 0;
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
                                            Bundle bundle = new Bundle();
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

        // 이전 화면에서 전달 받은 게시글 데이터
        Intent intent = getIntent();
        data = (PostData) intent.getSerializableExtra("data");
        mRef = FirebaseDatabase.getInstance().getReference();
        customProgressDialog = new CustomProgressDialog(PostContentActivity.this);
        imageContainer = findViewById(R.id.content_container);
        writerNicknameTextView = findViewById(R.id.content_profile_nickname);
        titleTextView = findViewById(R.id.content_title);
        dateTextView = findViewById(R.id.content_write_date);
        bodyTextView = findViewById(R.id.content_body);
        replyContainer = findViewById(R.id.reply_container);
        replyRecyclerView = findViewById(R.id.reply_recycler_view);
        userProfileImage = findViewById(R.id.content_user_profile);
        groupJoinBtn = findViewById(R.id.joinBtn);

        if (!User.currentUser.getUid().equals(data.getWriter().getUid())) {
            groupJoinBtn.setVisibility(View.VISIBLE);
        }

        groupJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> update = new HashMap<>();
                mRef.child("bulletin").push();
                String key = data.getPostID();


                //create group 할 경우 본인의 FCM 토큰, uid, username을 database의 채팅 메타데이터에 저장함
                String userId = User.currentUser.getUid();
                String userName = User.currentUser.getUserName();

                update.clear();
                update.put(userId, true);
                mRef.child("chat_meta" + "/" + key + "/" + "members").updateChildren(update);
                update.clear();
                update.put("conv_key", key);
                mRef.child("user_in" + "/" + userId).push().setValue(update);
                update.clear();
                update.put(userName, true);
                mRef.child("chat_meta").child(key).child("user_names").updateChildren(update);


                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }
                                // Get new FCM registration token
                                String token = task.getResult();
                                Map<String, Object> update = new HashMap<>();
                                update.put(userName, token);
                                mRef.child("chat_meta").child(key).child("FCM").updateChildren(update);
                            }
                        });

            }
        });

        replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        replyDataArrayList = new ArrayList<>();
        dividerItemDecoration = new DividerItemDecoration(replyRecyclerView.getContext(), new LinearLayoutManager(getBaseContext()).getOrientation());
        replyRecyclerView.addItemDecoration(dividerItemDecoration);

        if (User.currentUser.getUid().equals(data.getWriter().getUid())) {
            customProgressDialog.showLoadingDialog();
            Firestore.getAllReplyOnPostForOwner(data.getPostID()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        replyDataArrayList.clear();
                        int i = 0;
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            int finalI = i;
                            replyDataArrayList.add(documentSnapshot.toObject(ReplyData.class));
                            Firestore.getUserData(replyDataArrayList.get(finalI).getWriter().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        replyDataArrayList.get(finalI).setWriter(task.getResult().toObject(User.class));
                                        Bundle bundle = new Bundle();
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
                        customProgressDialog.dismissDialog();

                    }
                }
            });
        } else {
            customProgressDialog.showLoadingDialog();
            Firestore.getAllReplyOnPostForAnybody(data.getPostID()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        replyDataArrayList.clear();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            replyDataArrayList.add(documentSnapshot.toObject(ReplyData.class));
                        }
                        adapter = new ReplyAdapter(replyDataArrayList, getApplicationContext(), data.getPostType(), data.getWriter().getUid().equals(User.currentUser.getUid()));
                        replyRecyclerView.setAdapter(adapter);
                        customProgressDialog.dismissDialog();
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
                    customProgressDialog.showLoadingDialog();
                    ReplyData newData = new ReplyData(data.getPostID()
                            , data.getWriter().getUid()
                            , User.currentUser
                            , inputReply.getText().toString()
                            , false, 0);
                    replyDataArrayList.add(newData);
                    inputReply.setText("");
                    Firestore.addReplyData(newData)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        subtractPointByReplying();
                                        Firestore.insertReplyId(task.getResult().getId())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
//                                                            Toast.makeText(PostContentActivity.this, "success", Toast.LENGTH_SHORT).show();
                                                            Bundle bundle = new Bundle();

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostContentActivity.this);
                    builder.setTitle("댓글 작성")
                            .setMessage("이 게시글은 작성자가 댓글을 승인해야 정식으로 댓글이 게시됩니다. 작성하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
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
                                                        subtractPointByReplying();
                                                        Firestore.insertReplyId(task.getResult().getId())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            dialog.dismiss();
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
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            });
        } else {
            inflater.inflate(R.layout.reply_forbidden_layout, replyContainer);
        }

        Glide.with(getApplicationContext())
                .load(data.getWriter().getProfileUrl())
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userProfileImage);
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                intent.putExtra("uid", data.getWriter().getUid());
                startActivity(intent);
            }
        });
        writerNicknameTextView.setText(data.getWriter().getNickname());
        writerNicknameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                intent.putExtra("uid", data.getWriter().getUid());
                startActivity(intent);
            }
        });
        titleTextView.setText(data.getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd a HH:mm:ss");
        dateTextView.setText(simpleDateFormat.format(data.getWriteDate()));
        bodyTextView.setText(data.getBody());

        for (int i = 0; i < data.getPostImageUrls().size(); ++i) {
            ImageView imageView = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 55;
            imageContainer.addView(imageView, params);
            Glide.with(getApplicationContext())
                    .load(data.getPostImageUrls().get(i))
                    .fitCenter()
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(18)))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into((ImageView) imageContainer.getChildAt(i));
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_adopt:
                if (!replyDataArrayList.get(item.getGroupId()).isSelected()) {
                    if (User.currentUser.getUid().equals(replyDataArrayList.get(item.getGroupId()).getPostWriterID())
                            && !User.currentUser.getUid().equals(replyDataArrayList.get(item.getGroupId()).getWriter().getUid())) {
                        Log.d("메뉴 클릭", "채택");
                        Firestore.updateReplySelection(replyDataArrayList.get(item.getGroupId()).getReplyID())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(PostContentActivity.this, "채택 성공", Toast.LENGTH_SHORT).show();
                                            ReplyData current = replyDataArrayList.get(item.getGroupId());
                                            current.setSelected(true);
                                            adapter.setItem(item.getGroupId(), current);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(PostContentActivity.this, "채택 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        Firestore.updateUserPoint(replyDataArrayList.get((item.getGroupId()))
                                .getWriter().getUid(), data.getAllocPoint() * 2)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("update user point by reply adoption", "success");
                                        } else {
                                            Log.d("update user point by reply adoption", "failure");
                                        }
                                    }
                                });
                        boolean existSelected = false;
                        for (int i = 0; i < replyDataArrayList.size(); ++i) {
                            if (replyDataArrayList.get(i).isSelected()) {
                                existSelected = true;
                                break;
                            }
                        }
                        int point = 0;
                        if (existSelected) {
                            point = data.getAllocPoint() * -1;
                        } else {
                            point = (int) (data.getAllocPoint() * 1.2);
                        }
                        Firestore.updateUserPoint(data.getWriter().getUid(), point)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("update post owner point by adoption", "success");
                                        } else {
                                            Log.d("update post owner point by adoption", "failure");
                                        }
                                    }
                                });
                        Firestore.updateUserReliability(true, replyDataArrayList.get(item.getGroupId()).getWriter().getUid())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("update user reliability by reply", "success");
                                        } else {
                                            Log.d("update user reliability by reply", "failure");
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(this, "본인을 채택할 수 없습니다!", Toast.LENGTH_SHORT).show();
                        Log.d("메뉴 클릭 ", "소유자 아니거나 본인글에 대한 본인 댓글");
                    }
                }
                return true;
            case R.id.action_delete:
                if (User.currentUser.getUid().equals(replyDataArrayList.get(item.getGroupId()).getPostWriterID())
                        || User.currentUser.getUid().equals(replyDataArrayList.get(item.getGroupId()).getWriter().getUid())) {
                    Log.d("메뉴 클릭", "삭제");
                    Firestore.removeReplyOnPostByOwner(replyDataArrayList.get(item.getGroupId()).getReplyID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PostContentActivity.this, "삭제 성공", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    adapter.removeItem(item.getGroupId());
                } else {
                    Toast.makeText(this, "본인외의 댓글을 삭제할 수 없습니다!", Toast.LENGTH_SHORT).show();
                    Log.d("메뉴 클릭", "삭제 - 소유자 아니거나 댓글 작성자 아님");
                }
                return true;
            case R.id.action_report:
                if (!User.currentUser.getUid().equals(replyDataArrayList.get(item.getGroupId()).getWriter().getUid())) {
                    Firestore.updateUserReliability(false, replyDataArrayList.get(item.getGroupId()).getWriter().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PostContentActivity.this, "신고 성공(신뢰도 차감)", Toast.LENGTH_SHORT).show();

                                checkSum = 0;
                                Firestore.getAllReplyOnPostForOwner(data.getPostID()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            replyDataArrayList.clear();
                                            int i = 0;
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
                                                            Bundle bundle = new Bundle();
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
                                        }
                                    }
                                });
                            } else {
                                task.getException().printStackTrace();
                                Toast.makeText(PostContentActivity.this, "신고 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Log.d("메뉴 클릭", "신고");
                } else {
                    Toast.makeText(this, "본인을 신고할 수 없습니다!", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void subtractPointByReplying() {
        int point = 0;
        if (data.getPostType() == 0) {
            point = 20;
        } else {
            point = data.getAllocPoint() * -1;
        }
        Firestore.updateUserPoint(User.currentUser.getUid(), point)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("updated user point by reply", "success");
                        } else {
                            Log.d("updated user point by reply", "failure");
                        }
                    }
                });
    }
}