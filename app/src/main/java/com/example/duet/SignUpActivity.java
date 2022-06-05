package com.example.duet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.duet.fragment.MainMenuActivity;
import com.example.duet.util.FireStorage;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputUserName;
    private EditText inputNickname;

    private Button joinButton;
    private FirebaseAuth firebaseAuth;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        bindingView();
        Intent intent = getIntent();
        if (intent != null) {
            token = intent.getStringExtra("token");
        }
        firebaseAuth = FirebaseAuth.getInstance();
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String userName = inputUserName.getText().toString();
                String nickname = inputNickname.getText().toString();
                doSignUp(email, password, userName, nickname);
            }
        });
    }

    /**
     * SignUpActivity 의 모든 View 들을 초기화 및 바인딩하는 작업
     *
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
     *
     * @author Seunggun Sin, 2022-05-01
     */
    private void doSignUp(String email, String password, String userName, String nickname) {
        if (email.isEmpty()) {
            showToast("이메일을 입력하세요!");
            return;
        }
        if (!email.contains("@")) {
            showToast("이메일 형식이 아닙니다!");
            return;
        }
        if (password.length() < 8) {
            showToast("비밀번호를 8자리 이상 입력해주세요!");
            return;
        }
        if (userName.isEmpty()) {
            showToast("이름을 입력해주세요!");
            return;
        }
        if (nickname.isEmpty()) {
            showToast("닉네임을 입력해주세요!");
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 유저 생성에 성공했을 때
                        if (task.isSuccessful()) {
                            String userId = firebaseAuth.getCurrentUser().getUid(); // 생성한 유저의 uid 값 가져오기

                            Drawable drawable = getResources().getDrawable(R.drawable.ic_profile_foreground);
                            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                            drawable.draw(canvas);

                            FireStorage.uploadUserProfileImage(bitmap
                                    , userId)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                task.getResult().getStorage().getDownloadUrl()
                                                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                if (task.isSuccessful()) {
                                                                    Firestore.createNewUser(userId
                                                                            , inputEmail.getText().toString()
                                                                            , inputNickname.getText().toString()
                                                                            , inputUserName.getText().toString()
                                                                            , task.getResult().toString()
                                                                            , token)
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    // 데이터 생성 성공
                                                                                    if (task.isSuccessful()) {
                                                                                        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                                                                                        intent.putExtra("uid", userId);
                                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                        startActivity(intent);
                                                                                    } else { // 데이터 생성 실패
                                                                                        Log.d("sign up failure", "failure");
                                                                                        firebaseAuth.signOut();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Log.e("upload profile image", "failure");
                                            }
                                        }
                                    });
                            // Firestore 에 새로운 유저 데이터 저장

                        } else {
                            // 유저 생성 실패
                            showToast("회원가입에 실패했습니다!");
                        }
                    }
                });
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}