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
import android.widget.Toast;

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
        if (intent != null) {
            token = intent.getStringExtra("token");
        }

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignIn(inputEmail.getText().toString(), inputPassword.getText().toString());
            }
        });
    }

    /**
     * SignInActivity 의 모든 View 들을 초기화 및 바인딩하는 작업
     *
     * @author Seunggun Sin, 2022-05-01
     */
    private void bindingView() {
        inputEmail = findViewById(R.id.input_email_sign_in);
        inputPassword = findViewById(R.id.input_password_sign_in);
        loginButton = findViewById(R.id.btn_login);
    }

    /**
     * 유저로부터 입력받은 이메일과 비밀번호로 로그인 처리 요청
     *
     * @author Seunggun Sin, 2022-05-01
     */
    protected void doSignIn(String id, String password) {
        if (id.isEmpty()) {
            Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(id, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = firebaseAuth.getCurrentUser().getUid();

                            putAutoLogin(id, password);
                            Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                            intent.putExtra("uid", uid);
                            intent.putExtra("token", token);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignInActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    protected void putAutoLogin(String id, String password) {
        SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor autoLoginEdit = auto.edit();
        autoLoginEdit.putString("id", id);
        autoLoginEdit.putString("password", password);
        autoLoginEdit.commit();
    }

}