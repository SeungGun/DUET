package com.example.duet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.duet.fragment.MainMenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        bindingView();
        Intent intent = getIntent();
        if(intent != null){
            token = intent.getStringExtra("token");
        }

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putAutoLogin(inputEmail.getText().toString(), inputPassword.getText().toString());
                doSignIn(inputEmail.getText().toString(), inputPassword.getText().toString());
            }
        });
    }

    /**
     * SignInActivity 의 모든 View 들을 초기화 및 바인딩하는 작업
     * @author Seunggun Sin, 2022-05-01
     */
    private void bindingView() {
        inputEmail = findViewById(R.id.input_email_sign_in);
        inputPassword = findViewById(R.id.input_password_sign_in);
        loginButton = findViewById(R.id.btn_login);
    }

    /**
     * 유저로부터 입력받은 이메일과 비밀번호로 로그인 처리 요청
     * @author Seunggun Sin, 2022-05-01
     */
    private void doSignIn(String id, String password) {
        /*
            입력 defensive 처리 필요
         */
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

    protected void putAutoLogin(String id, String password){
        SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor autoLoginEdit = auto.edit();
        autoLoginEdit.putString("id", id);
        autoLoginEdit.putString("password", password);
        autoLoginEdit.commit();
    }

}