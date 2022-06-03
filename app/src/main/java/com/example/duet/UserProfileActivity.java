package com.example.duet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.duet.model.User;
import com.example.duet.util.Firestore;
import com.example.duet.util.LevelSystem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserProfileActivity extends AppCompatActivity {
    private User user;
    private ImageView profileImage;
    private TextView nicknameTextView;
    private TextView levelText;
    private ProgressBar levelProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            String uid = intent.getStringExtra("uid");
            Firestore.getUserData(uid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        user = task.getResult().toObject(User.class);
                        setContentView(R.layout.activity_user_profile);
                        profileImage = findViewById(R.id.profile_photo);
                        nicknameTextView = findViewById(R.id.profile_nickname);
                        levelText = findViewById(R.id.profile_level);
                        levelProgress = findViewById(R.id.level_progress);
                        nicknameTextView.setText(user.getNickname());
                        levelText.setText("Lv " + user.getLevel() + " ("+user.getExp()+"/"+ LevelSystem.expCumulativeList[user.getLevel() + 1]+")");
                        levelProgress.setProgress(Math.toIntExact(Math.round(
                                (user.getExp() * 1.0 - (int) LevelSystem.expCumulativeList[user.getLevel()])
                                        / ((int) LevelSystem.expCumulativeList[user.getLevel() + 1] - (int) LevelSystem.expCumulativeList[user.getLevel()])
                                        * 100)));
                        Glide.with(getApplicationContext())
                                .load(user.getProfileUrl())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profileImage);
                    }
                }
            });
        }
    }
}