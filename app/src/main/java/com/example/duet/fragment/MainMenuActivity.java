package com.example.duet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.duet.R;
import com.example.duet.model.User;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainMenuActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MainMenuBulletinFragment fragmentBulletin = new MainMenuBulletinFragment();
    private MainMenuProfileFragment fragmentProfile = new MainMenuProfileFragment();
    private MainMenuSettingFragment fragmentSetting = new MainMenuSettingFragment();
    private MainMenuStudyRoomFragment fragmentStudyRoom = new MainMenuStudyRoomFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Intent intent = getIntent();
        if(intent != null){
            String uid = intent.getStringExtra("uid");
            String token = intent.getStringExtra("token");
            Firestore.getUserData(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    User.currentUser = user;
                    if(User.currentUser.getToken() == null){
                        Firestore.updateUserToken(User.currentUser.getUid(), token)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainMenuActivity.this, "토큰 업데이트", Toast.LENGTH_SHORT).show();
                                    User.currentUser.setToken(token);
                                }
                            }
                        });
                    }
                }
            });
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.menu_frame_layout, fragmentBulletin, "0").commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

    }

    public boolean isLeft(int idx) {
        boolean flag = false;
        for (int i = 0; i < idx; i++) {
            if (fragmentManager.findFragmentByTag(i + "") != null){
                flag = true;
            }
        }

        return flag;
    }


    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            if (fragmentManager.findFragmentByTag(1 + "") != null){
                Log.d("frag", "1");
            }
            switch (menuItem.getItemId()) {
                case R.id.menu_bulletin:
                    if(isLeft(0)) {
                        transaction
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.menu_frame_layout, fragmentBulletin, "0").commitAllowingStateLoss();
                    }

                    else {
                        transaction
                                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                                .replace(R.id.menu_frame_layout, fragmentBulletin, "0").commitAllowingStateLoss();
                    }
                    break;
                case R.id.menu_study_room:

                    if(isLeft(1)) {
                        transaction
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.menu_frame_layout, fragmentStudyRoom, "1").commitAllowingStateLoss();
                    }

                    else {
                        transaction
                                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                                .replace(R.id.menu_frame_layout, fragmentStudyRoom, "1").commitAllowingStateLoss();
                    }

                    break;
                case R.id.menu_profile:

                    if(isLeft(2)) {
                        transaction
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.menu_frame_layout, fragmentProfile, "2").commitAllowingStateLoss();
                    }

                    else {
                        transaction
                                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                                .replace(R.id.menu_frame_layout, fragmentProfile, "2").commitAllowingStateLoss();
                    }

                    break;
                case R.id.menu_setting:

                    if(isLeft(3)) {
                        transaction
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.menu_frame_layout, fragmentSetting, "3").commitAllowingStateLoss();
                    }

                    else {
                        transaction
                                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
                                .replace(R.id.menu_frame_layout, fragmentSetting, "3").commitAllowingStateLoss();
                    }

                    break;
            }

            return true;
        }
    }
}