package com.example.duet.board;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.duet.R;
import com.example.duet.model.PostData;
import com.example.duet.model.User;
import com.example.duet.util.CategoryList;
import com.example.duet.util.CustomProgressDialog;
import com.example.duet.util.FireStorage;
import com.example.duet.util.Firestore;
import com.example.duet.util.LevelSystem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ????????? ???????????? ????????? ??? ?????? Activity
 *
 * @author Seungun Sin, 2022-05-02
 */
/*
    To Do List
    1. PostData ????????? ?????? ????????? ????????? ?????? v
    2. ????????? ?????????????????? ????????? ????????? Firestore ??? ?????? v
    3. ???????????? Firebase Storage ??? ?????? v
 */
public class CreatePostActivity extends AppCompatActivity {
    private LinearLayout imageContainer;
    private LinearLayout subtractPointLayout;
    private Button addImageButton;
    private EditText inputTitle;
    private EditText inputBody;
    private EditText inputSubtractPoint;
    private Button uploadButton;
    private RadioButton radioAlwaysButton;
    private RadioButton radioOptionalButton;
    private RadioButton radioNeverButton;
    private RadioGroup replyRadioGroup;
    private RadioGroup typeRadioGroup;
    private RadioButton radioActivityButton;
    private RadioButton radioQuestionButton;
    private Button createGroupButton;
    private ArrayList<String> imgUrlList;
    public static final int REQUEST_CODE = 0;
    public static final int URI_REQUEST_CODE = 7;
    public static final int INITIAL_POST_POINT = 200;
    private int checkSum = 0;
    private int inputLimitGroupCount = -1;
    private Bundle bundle;
    private CustomProgressDialog progressDialog;
    private ListView categoryListView;
    private ArrayList<String> selectedCategoryList;
    private DatabaseReference mRef;
    private Handler handler = new Handler(Looper.myLooper()) {
        /**
         * ???????????? ??????????????? ????????? ???????????? Storage ??? ????????? ??? message sign ??? ?????? ???
         * ?????? ?????? ????????? ????????? ????????? ????????? ???????????? ????????? ?????? Firestore ??? Post Data ?????? ??????
         * @param msg ???????????? ?????? ????????? ?????????(?????????)
         * @author Seunggun Sin, 2022-05-06
         */
        @Override
        public void handleMessage(Message msg) {
            checkSum += msg.getData().getInt("finish_count");

            if (imageContainer.getChildCount() == checkSum) {
                // Firestore ??? ????????? url ???????????? ????????? ???????????? ?????? Post ????????? ?????? ??????
                int id = replyRadioGroup.getCheckedRadioButtonId();
                int state = 0;
                if (radioAlwaysButton.getId() == id) {
                    state = 0;
                } else if (radioOptionalButton.getId() == id) {
                    state = 1;
                } else if (radioNeverButton.getId() == id) {
                    state = 2;
                }

                int id2 = typeRadioGroup.getCheckedRadioButtonId();
                int typeState = 0;
                int point = 0;
                if (radioQuestionButton.getId() == id2) {
                    typeState = 1;
                    point = Integer.parseInt(inputSubtractPoint.getText().toString());
                    state = 0;
                }
                SparseBooleanArray checkItems = categoryListView.getCheckedItemPositions();

                for (int i = 0; i < CategoryList.items.length; ++i) {
                    if (checkItems.get(i)) {
                        selectedCategoryList.add(CategoryList.items[i]);
                    }
                }
                // ????????? ????????? ????????? ?????? ??????
                Firestore.createNewPost(
                        new PostData(User.currentUser
                                , inputTitle.getText().toString()
                                , selectedCategoryList
                                , inputBody.getText().toString()
                                , point
                                , state
                                , imgUrlList
                                , typeState
                                , inputLimitGroupCount)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d("create post", "success, id:" + task.getResult().getId());
                            String id = task.getResult().getId();
                            // ????????? ????????? ???????????? post id ?????? ??? ?????????
                            Firestore.insertPostId(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("pid update", "success");
                                        if (inputLimitGroupCount != -1) {
                                            createGroupSetting(id);
                                        }

                                    } else {

                                    }
                                }
                            });
                            updateUserPostCountToday();
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
        selectedCategoryList = new ArrayList<>();
        bundle = new Bundle();
        mRef = FirebaseDatabase.getInstance().getReference();
        progressDialog = new CustomProgressDialog(CreatePostActivity.this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, CategoryList.items);
        categoryListView.setAdapter(arrayAdapter);
        setListViewHeightBasedOnChildren(categoryListView);
        radioActivityButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    subtractPointLayout.setVisibility(View.GONE);
                    replyRadioGroup.setVisibility(View.VISIBLE);
                } else {
                    subtractPointLayout.setVisibility(View.VISIBLE);
                    replyRadioGroup.setVisibility(View.GONE);
                }
            }
        });
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout container = new FrameLayout(getApplicationContext());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 60;
                params.rightMargin = 60;
                EditText editText = new EditText(getApplicationContext());
                editText.setHint("????????? ?????? ?????? ???????????????.");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setLayoutParams(params);
                if (inputLimitGroupCount != -1) {
                    editText.setText(inputLimitGroupCount + "");
                }
                container.addView(editText);
                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);
                builder.setTitle("?????? ?????? ?????? ??????")
                        .setCancelable(false)
                        .setView(container)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                inputLimitGroupCount = Integer.parseInt(editText.getText().toString());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("?????? ??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                inputLimitGroupCount = -1;
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // implicit intent ??? ???????????? ????????? ?????? ???????????? ???????????? ????????? ??? ????????????
                Intent intent = new Intent();
                intent.setType("image/* video/*"); // ?????? ?????? ?????? (?????????, ?????????)
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputTitle.getText().toString().isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "????????? ???????????????!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (inputBody.getText().toString().isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "????????? ???????????????!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (inputSubtractPoint.getText().toString().isEmpty() && radioQuestionButton.isChecked()) {
                    Toast.makeText(CreatePostActivity.this, "????????? ???????????? ???????????????!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    OutputStreamWriter out = new OutputStreamWriter(openFileOutput("offline.txt", 0));
                    out.write("");
                    out.close();
                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), "Exception: " + t.toString(), Toast.LENGTH_SHORT).show();
                }

                progressDialog.showLoadingDialog();
                new Thread() {
                    @Override
                    public void run() {
                        uploadImagesToStorage();
                    }

                }.start();
            }
        });

        readOffline();

    }

    /**
     * CreatePostActivity ??? ?????? ?????? View ????????? ??? ????????? ?????? ??????
     *
     * @author Seunggun Sin, 2022-05-06
     */
    private void bindingView() {
        imageContainer = findViewById(R.id.container);
        addImageButton = findViewById(R.id.btn_add_image);
        inputTitle = findViewById(R.id.input_title);
        inputBody = findViewById(R.id.input_body);
        inputSubtractPoint = findViewById(R.id.input_alloc_point);
        uploadButton = findViewById(R.id.upload_post_btn);
        replyRadioGroup = findViewById(R.id.reply_range_radio_group);
        radioAlwaysButton = findViewById(R.id.radio_always);
        radioOptionalButton = findViewById(R.id.radio_optional);
        radioNeverButton = findViewById(R.id.radio_never);
        categoryListView = findViewById(R.id.category_list);
        createGroupButton = findViewById(R.id.create_group_btn);
        typeRadioGroup = findViewById(R.id.post_type_radio_group);
        radioActivityButton = findViewById(R.id.radio_activity);
        radioQuestionButton = findViewById(R.id.radio_question);
        subtractPointLayout = findViewById(R.id.sub_point_layout);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) { // ??????????????? ????????? ???????????? ????????? ?????? ??????
            if (resultCode == RESULT_OK) { // ????????? ????????? ??????
                try {
                    assert data != null;
                    Uri selectedImageUri = data.getData(); // ????????? ????????? ?????? ?????? ?????? ?????????

                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    InputStream inputStream2 = getContentResolver().openInputStream(selectedImageUri);

                    // ????????? ???????????? Bitmap ????????? ??????
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // ????????? ???????????? ?????? ????????? ?????? ???????????? ??????????????? ??? ??????
                    ExifInterface exifInterface = new ExifInterface(inputStream2);
                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                    // ?????? bitmap ??? ???????????? ????????? bitmap ?????? ??????
                    bitmap = rotateBitmap(bitmap, orientation);

                    inputStream.close();
                    inputStream2.close();

                    ImageView imageView = new ImageView(getApplicationContext()); // ?????? ImageView ??????
                    imageView.setImageBitmap(bitmap); // ImageView ??? ????????? ????????? ??????
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY); // ????????? ????????? ????????? ?????? ??????
                    imageView.setAdjustViewBounds(true); // ????????? ????????? ????????? ?????? ??????

                    // ?????? ???????????? ?????? ??????
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                            , LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.bottomMargin = 20;

                    // LinearLayout ??? child ??? ???????????? ImageView ???????????? ??????
                    imageContainer.addView(imageView, params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) { // ?????? ????????? ????????? ??????
                Toast.makeText(this, "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * ????????? ???????????? ImageView ?????? ???????????? ????????? ?????????, Bitmap ???????????? Exif ?????????????????? ????????? ????????? ???????????? ?????????(Bitmap) ??????
     *
     * @param bitmap      ?????? Bitmap ?????????
     * @param orientation ?????? ???????????? ?????? ?????? ?????? ???(?)
     * @return ????????? ???????????? ?????? Bitmap ??????
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
     * ????????? ????????? ???????????? ???????????? Storage ??? ???????????? ????????? ???
     * ????????? ????????? ????????????????????? ????????? handler ??? ?????? ??????
     *
     * @author Seunggun Sin, 2022-05-06
     */
    private void uploadImagesToStorage() {
        int imageCount = imageContainer.getChildCount();

        // ????????? ???????????? ???????????? count ??? 0??? ?????? ????????? ??????
        if (imageCount == 0) {
            bundle.putInt("finish_count", 0);
            Message msg = handler.obtainMessage();
            msg.setData(bundle);
            handler.sendMessage(msg); // ????????? ??????
            return;
        }
        for (int i = 0; i < imageCount; ++i) {
            // image ??? Storage ??? ???????????? ??????
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
                                                    // Storage ??? ????????? ?????? ?????? ?????? ???????????? List ??? ??????
                                                    imgUrlList.add(task.getResult().toString());

                                                    bundle.putInt("finish_count", 1);
                                                    Message msg = handler.obtainMessage();
                                                    msg.setData(bundle);
                                                    handler.sendMessage(msg); // ????????? ??????
                                                } else {
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
     * ViewGroup ??? ?????? child View, raw image view ??? Bitmap ?????? ??????
     *
     * @param image View ????????? ?????????(ImageView ??? downCasting ?????? ?????? view object)
     * @return Bitmap ?????? ????????? ?????????
     * @author Seunggun Sin, 2022-05-06
     */
    private Bitmap getBitmapFromViewImage(View image) {
        return ((BitmapDrawable) ((ImageView) image).getDrawable()).getBitmap();
    }

    /**
     * ????????? ???????????? ????????? ???????????? ?????? ????????? ????????? ????????????
     */
    public void updateUserPostCountToday() {
        Firestore.getUserAllPostData(User.currentUser.getUid())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = -1;
                            Calendar getToday = Calendar.getInstance();
                            getToday.setTime(new Date());

                            for (QueryDocumentSnapshot documentSnapshots : task.getResult()) {
                                PostData curPost = documentSnapshots.toObject(PostData.class);
                                Calendar cmpDate = Calendar.getInstance();
                                cmpDate.setTime(curPost.getWriteDate());
                                long diffSec = (getToday.getTimeInMillis() - cmpDate.getTimeInMillis()) / 1000;
                                long diffDay = diffSec / (24 * 60 * 60);
                                if (diffDay == 0) {
                                    count++;
                                }
                            }
                            int nextPoint = 0;
                            if (radioQuestionButton.isChecked()) {
                                nextPoint = Integer.parseInt(inputSubtractPoint.getText().toString());
                            } else {
                                nextPoint = LevelSystem.obtainNextPointForPost(count, INITIAL_POST_POINT);
                            }
                            int point = nextPoint;
                            Firestore.updateUserPoint(User.currentUser.getUid(), nextPoint)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                if (User.currentUser.getExp() + point > LevelSystem.expCumulativeList[User.currentUser.getLevel() + 1]) {
                                                    Firestore.updateUserLevel(User.currentUser.getUid())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        User.currentUser.setLevel(User.currentUser.getLevel() + 1);
                                                                        User.currentUser.setExp(User.currentUser.getExp() + point);
                                                                        progressDialog.dismissDialog();
                                                                        finish();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    progressDialog.dismissDialog();
                                                    finish();
                                                }
                                                /*
                                                    ????????? ????????? ????????? ??????
                                                 */
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * ???????????? ListView ??? contents ?????? ????????? ????????? ???????????? ??????
     *
     * @param listView ?????? ????????? listview
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void createGroupSetting(String pid) {
        Map<String, Object> update = new HashMap<>();
        mRef.child("bulletin").push();

        String key = pid;

        //TODO content, title null checking
        //TODO Button invisible to Bulletin board owner
        //TODO Clean up with data model object

        //create group ??? ?????? ????????? FCM ??????, uid, username??? database??? ?????? ?????????????????? ?????????

        String sendTitle = inputTitle.getText().toString();
        String userId = User.currentUser.getUid();
        String userName = User.currentUser.getUserName();

        update.clear();
        update.put("title", sendTitle);
        mRef.child("chat_meta" + "/" + key).setValue(update);
        update.clear();
        update.put(userId, true);
        mRef.child("chat_meta" + "/" + key + "/" + "members").setValue(update);
        update.clear();
        update.put("conv_key", key);
        mRef.child("user_in" + "/" + userId).push().setValue(update);
        update.clear();
        update.put(userName, true);
        mRef.child("chat_meta/" + key + "/user_names").setValue(update);


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        Map<String, Object> update = new HashMap<>();
                        update.put(userName, token);
                        mRef.child("chat_meta").child(key).child("FCM").updateChildren(update);
                    }
                });
    }

    private void readOffline() {
        String str = "";
        StringBuffer buf = new StringBuffer();

        try {
            InputStream in = openFileInput("offline.txt");
            if (in != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n");
                }
                in.close();

            }//if
            else
                return;
        } catch (java.io.FileNotFoundException e) {
            Log.d("aaaaaaa", String.valueOf(e));
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_SHORT).show();
        }

        if (buf.toString() == "")
            return;

        Log.d("aaaaaaa", buf.toString());


        JSONObject jObject = null;
        try {
            jObject = new JSONObject(buf.toString());
            String title = jObject.getString("title");
            String body = jObject.getString("body");
            String point = jObject.getString("point");
            String state = jObject.getString("state");
            String cat = jObject.getString("category");
            String img = jObject.getString("image");

            String[] category = cat.split(",");

            String[] image = img.split(",");

            inputTitle.setText(title);
            inputBody.setText(body);
            inputSubtractPoint.setText(point);

            if (Integer.parseInt(state) == 0)
                replyRadioGroup.check(R.id.radio_always);
            else if (Integer.parseInt(state) == 1)
                replyRadioGroup.check(R.id.radio_optional);
            else if (Integer.parseInt(state) == 2)
                replyRadioGroup.check(R.id.radio_never);

            for (String c : category) {
                categoryListView.setItemChecked(Integer.parseInt(c), true);
            }

            for (String i : image) {
                imageSet(Uri.parse(i));
            }

        } catch (JSONException e) {
            Log.d("aaaaaaa", String.valueOf(e));
        }
    }


    private void imageSet(Uri selectedImageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            InputStream inputStream2 = getContentResolver().openInputStream(selectedImageUri);

            // ????????? ???????????? Bitmap ????????? ??????
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // ????????? ???????????? ?????? ????????? ?????? ???????????? ??????????????? ??? ??????
            ExifInterface exifInterface = new ExifInterface(inputStream2);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            // ?????? bitmap ??? ???????????? ????????? bitmap ?????? ??????
            bitmap = rotateBitmap(bitmap, orientation);

            inputStream.close();
            inputStream2.close();

            ImageView imageView = new ImageView(getApplicationContext()); // ?????? ImageView ??????
            imageView.setImageBitmap(bitmap); // ImageView ??? ????????? ????????? ??????
            imageView.setScaleType(ImageView.ScaleType.FIT_XY); // ????????? ????????? ????????? ?????? ??????
            imageView.setAdjustViewBounds(true); // ????????? ????????? ????????? ?????? ??????

            // ?????? ???????????? ?????? ??????
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 20;

            // LinearLayout ??? child ??? ???????????? ImageView ???????????? ??????
            imageContainer.addView(imageView, params);
        } catch (Exception e) {
            Log.d("aaaaaaa", e.toString());
        }
    }

}

