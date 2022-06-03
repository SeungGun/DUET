package com.example.duet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.duet.board.CreatePostActivity;
import com.example.duet.board.OfflineCreatePostActivity;
import com.example.duet.fragment.MainMenuActivity;
import com.example.duet.model.User;
import com.example.duet.util.Firestore;
import com.example.duet.util.LevelSystem;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    String token;
    ConnectivityManager connectivityManager;

    Button btn_in;
    Button btn_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_up = findViewById(R.id.btn_sign_up);
        btn_in = findViewById(R.id.btn_sign_in);

        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        LevelSystem.initExp();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful()){
                            token = task.getResult();
                            btn_in.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    intent.putExtra("token", token);
                                    startActivity(intent);
                                }
                            });

                            btn_up.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                                    intent.putExtra("token", token);
                                    startActivity(intent);
                                }
                            });
                            if(checkInternetState())
                                doAutoLogin();
                            else
                                offlineMode();
                        }
                        else{
                            Log.w("token error", "Fetching FCM registration token failed", task.getException());
                        }
                    }
                });
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
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.signInWithEmailAndPassword(id, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uid = firebaseAuth.getCurrentUser().getUid();
                    /*
                        FCM 사용할 때 토큰 가져오는 것도 필요할 것 같음
                        firebaseAuth.getCurrentUser().getIdToken()
                     */
                                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                                intent.putExtra("uid", uid);
                                intent.putExtra("token", token);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {

                            }
                        }
                    });
        }
    }

    private boolean checkInternetState(){
        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        if(!(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()))
            return false;
        return true;
    }

    protected void offlineMode(){
        Button btn_offline = findViewById(R.id.btn_offline);
        btn_offline.setVisibility(View.VISIBLE);
        btn_in.setVisibility(View.INVISIBLE);
        btn_up.setVisibility(View.INVISIBLE);

        btn_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), OfflineCreatePostActivity.class));
            }
        });
    }
}