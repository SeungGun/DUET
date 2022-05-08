package com.example.duet.board;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.duet.R;
import com.example.duet.model.PostData;
import com.example.duet.model.User;
import com.example.duet.util.CustomProgressDialog;
import com.example.duet.util.FireStorage;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * 새로운 게시글을 추가할 수 있는 Activity
 *
 * @author Seungun Sin, 2022-05-02
 */
/*
    To Do List
    1. PostData 모델에 맞는 사용자 입력창 생성 v
    2. 이미지 메타데이터와 입력한 정보를 Firestore 에 저장 v
    3. 이미지를 Firebase Storage 에 저장 v
 */
public class CreatePostActivity extends AppCompatActivity {
    private LinearLayout imageContainer;
    private Button addImageButton;
    private EditText inputTitle;
    private EditText inputBody;
    private EditText inputSubtractPoint;
    private Button uploadButton;
    private Button showPostsButton;
    private ArrayList<String> imgUrlList;
    public static final int REQUEST_CODE = 0;
    private int checkSum = 0;
    private Bundle bundle;
    private CustomProgressDialog progressDialog;
    private Handler handler = new Handler(Looper.myLooper()) {
        /**
         * 이미지를 갤러리에서 추가한 개수만큼 Storage 에 저장할 때 message sign 을 받는 곳
         * 만약 총합 개수가 추가한 이미지 개수와 같아지게 된다면 그때 Firestore 에 Post Data 저장 요청
         * @param msg 핸들러를 통해 전달한 메세지(데이터)
         * @author Seunggun Sin, 2022-05-06
         */
        @Override
        public void handleMessage(Message msg) {
            checkSum += msg.getData().getInt("finish_count");

            if (imageContainer.getChildCount() == checkSum) {
                // Firestore 에 이미지 url 정보들과 입력한 데이터를 함께 Post 데이터 저장 요청
                Firestore.createNewPost(
                        new PostData(User.currentUser.getUid()
                                , inputTitle.getText().toString()
                                , imgUrlList, inputBody.getText().toString()
                                , Integer.parseInt(inputSubtractPoint.getText().toString())
                                , imgUrlList)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("create post", "success, id:"+task.getResult().getId());
                            Firestore.insertPostId(task.getResult().getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("pid update","success");
                                        progressDialog.dismissDialog();
                                    }
                                    else{

                                    }
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("create post", "failure");
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        bindingView();
        imgUrlList = new ArrayList<>();
        bundle = new Bundle();
        progressDialog = new CustomProgressDialog(CreatePostActivity.this);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // implicit intent 을 사용하여 갤러리 앱을 호출하여 이미지를 가져올 수 있도록함
                Intent intent = new Intent();
                intent.setType("image/* video/*"); // 파일 타입 지정 (이미지, 비디오)
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.showLoadingDialog();
                new Thread(){
                    @Override
                    public void run(){
                        uploadImagesToStorage();
                    }

                }.start();
            }
        });

        showPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TestShowPostActivity.class));
            }
        });
    }

    /**
     * CreatePostActivity 에 있는 모든 View 초기화 및 바인딩 하는 작업
     * @author Seunggun Sin, 2022-05-06
     */
    private void bindingView() {
        imageContainer = findViewById(R.id.container);
        addImageButton = findViewById(R.id.btn_add_image);
        inputTitle = findViewById(R.id.input_title);
        inputBody = findViewById(R.id.input_body);
        inputSubtractPoint = findViewById(R.id.input_alloc_point);
        uploadButton = findViewById(R.id.upload_post_btn);
        showPostsButton = findViewById(R.id.btn_show_posts);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) { // 갤러리에서 사진을 가져오는 상황에 대한 처리
            if (resultCode == RESULT_OK) { // 사진이 선택된 경우
                try {
                    assert data != null;
                    Uri selectedImageUri = data.getData(); // 선택한 사진에 대한 경로 값을 가져옴

                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    InputStream inputStream2 = getContentResolver().openInputStream(selectedImageUri);

                    // 선택한 이미지를 Bitmap 형태로 생성
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // 선택한 이미지의 절대 경로를 통해 이미지의 메타데이터 값 추출
                    ExifInterface exifInterface = new ExifInterface(inputStream2);
                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    // 원본 bitmap 을 올바르게 회전한 bitmap 으로 변환
                    bitmap = rotateBitmap(bitmap, orientation);

                    inputStream.close();
                    inputStream2.close();

                    ImageView imageView = new ImageView(getApplicationContext()); // 동적 ImageView 생성
                    imageView.setImageBitmap(bitmap); // ImageView 로 보여줄 이미지 설정
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY); // 깔끔한 공간과 비율을 위해 필요
                    imageView.setAdjustViewBounds(true); // 깔끔한 공간과 비율을 위해 필요

                    // 동적 레이아웃 속성 지정
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                            , LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.bottomMargin = 20;

                    // LinearLayout 의 child 로 새로만든 ImageView 동적으로 추가
                    imageContainer.addView(imageView, params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) { // 사진 선택이 취소된 경우
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 불러온 이미지가 ImageView 에서 회전되는 현상이 있어서, Bitmap 자체에서 Exif 메타데이터를 이용해 제대로 회전시킨 이미지(Bitmap) 반환
     *
     * @param bitmap      원본 Bitmap 데이터
     * @param orientation 원본 이미지의 현재 회전 상태 값(?)
     * @return 제대로 회전시킨 뒤의 Bitmap 반환
     * @author Seunggun Sin, 2022-05-02
     */
    public Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;

        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return bmRotated;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * 추가한 이미지 개수만큼 반복하여 Storage 에 저장하는 요청을 함
     * 동시에 저장이 완료되었다라는 신호를 handler 를 통해 전달
     *
     * @author Seunggun Sin, 2022-05-06
     */
    private void uploadImagesToStorage() {
        int imageCount = imageContainer.getChildCount();
        for (int i = 0; i < imageCount; ++i) {
            // image 를 Storage 에 저장하는 요청
            FireStorage.uploadPostImage(getBitmapFromViewImage(imageContainer.getChildAt(i))
                    , User.currentUser.getUid())
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        task.getResult().getStorage().getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    // Storage 에 저장한 경로 주소 값을 받아와서 List 에 저장
                                    imgUrlList.add(task.getResult().toString());

                                    bundle.putInt("finish_count", 1);
                                    Message msg = handler.obtainMessage();
                                    msg.setData(bundle);
                                    handler.sendMessage(msg); // 메세지 전달
                                }
                                else{
                                    Log.e("get image download url", "Failure");
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * ViewGroup 에 있는 child View, raw image view 를 Bitmap 으로 변환
     *
     * @param image View 타입의 이미지(ImageView 로 downCasting 되지 않은 view object)
     * @return Bitmap 으로 변환된 이미지
     * @author Seunggun Sin, 2022-05-06
     */
    private Bitmap getBitmapFromViewImage(View image) {
        return ((BitmapDrawable) ((ImageView) image).getDrawable()).getBitmap();
    }
}

