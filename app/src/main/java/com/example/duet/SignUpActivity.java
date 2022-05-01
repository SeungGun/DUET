package com.example.duet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputUserName;
    private EditText inputNickname;

    private Button joinButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        bindingView();
        firebaseAuth = FirebaseAuth.getInstance();
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignUp();
            }
        });
    }

    /**
     * SignUpActivity 의 모든 View 들을 초기화 및 바인딩하는 작업
     * @author Seunggun Sin, 2022-05-01
     */
    private void bindingView() {
        inputEmail = findViewById(R.id.input_email_sign_up);
        inputPassword = findViewById(R.id.input_password_sign_up);
        inputUserName = findViewById(R.id.input_user_name);
        inputNickname = findViewById(R.id.input_nickname);
        joinButton = findViewById(R.id.btn_join);
    }

    /**
     * 사용자로부터 입력받은 4개의 필드로 새로운 사용자를 생성하고 DB에 저장하여 회원가입하도록 함
     * @author Seunggun Sin, 2022-05-01
     */
    private void doSignUp() {
        /*
           입력 내용 defensive 처리 필요
         */
        firebaseAuth.createUserWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 유저 생성에 성공했을 때
                        if (task.isSuccessful()) {
                            String userId = firebaseAuth.getCurrentUser().getUid(); // 생성한 유저의 uid 값 가져오기

                            // Firestore 에 새로운 유저 데이터 저장
                            Firestore.createNewUser(userId
                                    , inputEmail.getText().toString()
                                    , inputNickname.getText().toString()
                                    , inputUserName.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // 데이터 생성 성공
                                    if (task.isSuccessful()) {
                                        Log.d("sign up success", "sucess");
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    } else { // 데이터 생성 실패
                                        Log.d("sign up failure", "failure");
                                        firebaseAuth.signOut();
                                    }
                                }
                            });
                        } else {
                            // 유저 생성 실패
                        }
                    }
                });
    }
}