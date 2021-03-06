package com.example.duet.board;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
public class OfflineCreatePostActivity extends AppCompatActivity {
    private LinearLayout imageContainer;
    private Button addImageButton;
    private EditText inputTitle;
    private EditText inputBody;
    private EditText inputSubtractPoint;
    private Button uploadButton;
    private RadioButton radioAlwaysButton;
    private RadioButton radioOptionalButton;
    private RadioButton radioNeverButton;
    private ArrayList<String> imgUrlList;
    public static final int REQUEST_CODE = 0;
    public static final int INITIAL_POST_POINT = 200;
    private int checkSum = 0;
    int state;
    ArrayList<String> img = new ArrayList<>();

    private Bundle bundle;
    private CustomProgressDialog progressDialog;
    private RadioGroup radioGroup;
    private ListView categoryListView;
    private ArrayList<String> selectedCategoryList;
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
                int id = radioGroup.getCheckedRadioButtonId();

                state = 0;
                if (radioAlwaysButton.getId() == id) {
                    state = 0;
                } else if (radioOptionalButton.getId() == id) {
                    state = 1;
                } else if (radioNeverButton.getId() == id) {
                    state = 2;
                }

                SparseBooleanArray checkItems = categoryListView.getCheckedItemPositions();

                for (int i = 0; i < CategoryList.items.length; ++i) {
                    if (checkItems.get(i)) {
                        selectedCategoryList.add(CategoryList.items[i]);
                        Log.d("ddddddd", CategoryList.items[i]);
                    }
                }
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

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, CategoryList.items);
        categoryListView.setAdapter(arrayAdapter);
        setListViewHeightBasedOnChildren(categoryListView);

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

        uploadButton.setText("off-line Save");
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();

                String title = inputTitle.getText().toString();
                Log.d("dddddddd", title);

                String body = inputBody.getText().toString();

                String point = inputSubtractPoint.getText().toString();

                int id = radioGroup.getCheckedRadioButtonId();

                state = 0;
                if (radioAlwaysButton.getId() == id) {
                    state = 0;
                } else if (radioOptionalButton.getId() == id) {
                    state = 1;
                } else if (radioNeverButton.getId() == id) {
                    state = 2;
                }

                String cat = "";
                String image = "";

                SparseBooleanArray checkItems = categoryListView.getCheckedItemPositions();

                for (int i = 0; i < CategoryList.items.length; ++i) {
                    if (checkItems.get(i)) {
                        selectedCategoryList.add(Integer.toString(i));
                        Log.d("ddddddd", CategoryList.items[i]);
                    }
                }

                try {
                    jsonObject.put("title", title);
                    jsonObject.put("body", body);
                    jsonObject.put("point", point);
                    jsonObject.put("state", Integer.toString(state));

                    for(String s : selectedCategoryList){
                        if(cat.equals(""))
                            cat += s;
                        else
                            cat += "," + s;
                    }
                    jsonObject.put("category", cat);

                    for(String s : img){
                        if(image == "")
                            image += s;
                        else
                            image += "," + s;
                    }
                    jsonObject.put("image", image);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch(NullPointerException e){
                    Log.d("dddddddd", String.valueOf(e));
                }

                String jsonString = jsonObject.toString();
                Log.d("aaa", jsonString);

                try {
                    OutputStreamWriter out = new OutputStreamWriter(openFileOutput("offline.txt", 0));
                    out.write(jsonString);
                    out.close();
                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), "Exception: " + t.toString(), Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
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
        radioGroup = findViewById(R.id.reply_range_radio_group);
        radioAlwaysButton = findViewById(R.id.radio_always);
        radioOptionalButton = findViewById(R.id.radio_optional);
        radioNeverButton = findViewById(R.id.radio_never);
        categoryListView = findViewById(R.id.category_list);
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

                    img.add(selectedImageUri.toString());

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

}

