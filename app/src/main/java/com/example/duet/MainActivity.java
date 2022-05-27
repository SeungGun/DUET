package com.example.duet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.duet.board.CreatePostActivity;
import com.example.duet.model.User;
import com.example.duet.util.Firestore;
import com.example.duet.util.LevelSystem;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private Button createPostButton;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LevelSystem.initExp();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()){
                            token = task.getResult();
                            findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    intent.putExtra("token", token);
                                    intent.putExtra("auto", false);
                                    startActivity(intent);
                                }
                            });
                            findViewById(R.id.btn_sign_up).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                                    intent.putExtra("token", token);
                                    startActivity(intent);
                                }
                            });
                        }
                        else{
                            Log.w("token error", "Fetching FCM registration token failed", task.getException());
                        }
                    }
                });

        doAutoLogin();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                FirebaseAuth.getInstance().signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void doAutoLogin() {
        SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        String id = auto.getString("id", null);
        String password = auto.getString("password", null);

        if (id != null && password != null) {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("auto", true);
            startActivity(intent);
        }
    }
}