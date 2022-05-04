package com.example.duet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.duet.R;
import com.example.duet.model.User;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnSuccessListener;
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
            Firestore.getUserData(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    User.currentUser = user;
                }
            });
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.menu_frame_layout, fragmentBulletin).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()) {
                case R.id.menu_bulletin:
                    transaction.replace(R.id.menu_frame_layout, fragmentBulletin).commitAllowingStateLoss();
                    break;
                case R.id.menu_study_room:
                    transaction.replace(R.id.menu_frame_layout, fragmentStudyRoom).commitAllowingStateLoss();
                    break;
                case R.id.menu_profile:
                    transaction.replace(R.id.menu_frame_layout, fragmentProfile).commitAllowingStateLoss();
                    break;
                case R.id.menu_setting:
                    transaction.replace(R.id.menu_frame_layout, fragmentSetting).commitAllowingStateLoss();
                    break;
            }

            return true;
        }
    }
}