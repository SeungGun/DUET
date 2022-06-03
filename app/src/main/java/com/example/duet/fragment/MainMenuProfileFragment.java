package com.example.duet.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.duet.myPost.ProfileMyPost;
import com.example.duet.R;
import com.example.duet.board.TestUpdateProfile;
import com.example.duet.model.PostData;
import com.example.duet.model.User;
import com.example.duet.util.FireStorage;
import com.example.duet.util.Firestore;
import com.example.duet.util.LevelSystem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Collections;

public class MainMenuProfileFragment extends Fragment {

    private ImageView profileImage;
    private ArrayList<PostData> userPostList;
    private ProgressBar levelProgress;
    private TextView levelText;
    private TextView nicknameText;
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 유저 프로필 이미지 업데이트와 url 업데이트가 끝난 뒤 메세지를 받으면 유저 데이터 갱신 요청
            Firestore.getUserData(User.currentUser.getUid())
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                // 요청한 유저 데이터를 현재 유저로 변경
                                User.currentUser = task.getResult().toObject(User.class);
                                // 새로운 프로필 이미지로 다시 보여주기
                                Glide.with(getActivity())
                                        .load(User.currentUser.getProfileUrl())
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(profileImage);
                            }
                        }
                    });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu_profile, container, false);

        profileImage = rootView.findViewById(R.id.profile_photo);
        userPostList = new ArrayList<>();
        profileImage.setScaleType(ImageView.ScaleType.FIT_XY); // 깔끔한 공간과 비율을 위해 필요
        profileImage.setAdjustViewBounds(true); // 깔끔한 공간과 비율을 위해 필요
        levelProgress = rootView.findViewById(R.id.level_progress);
        levelText = rootView.findViewById(R.id.profile_level);
        nicknameText = rootView.findViewById(R.id.profile_nickname);
        nicknameText.setText(User.currentUser.getNickname());
        setLevelProgress();
        /*
         현재 사용자의 모든 게시글 데이터를 가져오는 요청 {userPostList 이름의 ArrayList 에 PostData 저장}
         */
        Firestore.getUserAllPostData(User.currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        userPostList.add(documentSnapshot.toObject(PostData.class));
                    }
                }
            }
        });

        // 현재 사용자의 데이터에 있는 프로필 이미지 url 을 통해 프로필 이미지를 보여줌
        Glide.with(getActivity())
                .load(User.currentUser.getProfileUrl())
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profileImage);

        // 프로필 ImageView 클릭 시, 프로필 이미지 변경에 대한 다이얼로그 표시
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("프로필 변경");
                String[] items = {"갤러리에서 가져오기", "기본 이미지로 변경"};

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // "갤러리에서 가져오기" 선택 시, {TestUpdateProfile 액티비티로 이동}
                        if (which == 0) {
                            startActivity(new Intent(getActivity(), TestUpdateProfile.class));
                        }
                        // "기본 이미지로 변경" 선택 시
                        else {
                            // 기본 이미지로 사용자 프로필 이미지 변경 요청(새로운 이미지 저장하는 요청)
                            FireStorage.uploadUserProfileImage(getDefaultProfileBitmap()
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
                                                                    Toast.makeText(getActivity(), "이미지 업데이트 성공", Toast.LENGTH_SHORT).show();
                                                                    // 이미지 저장 성공 시, 해당 이미지에 대한 url 을 가져와서 Firestore 에도 프로필 url 업데이트 요청
                                                                    Firestore.updateUserProfileUrl(User.currentUser.getUid()
                                                                            , task.getResult().toString())
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        // url 업데이트 성공 시, 핸들러를 통해 유저 데이터 갱신을 위한 메세지 전달
                                                                                        Message message = handler.obtainMessage();
                                                                                        handler.sendMessage(message);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        MaterialCalendarView materialCalendarView = rootView.findViewById(R.id.profile_calendar);
        materialCalendarView.setSelectedDate(CalendarDay.today());

        // 점찍기
        materialCalendarView.addDecorator(new EventDecorator(Color.RED, Collections.singleton(CalendarDay.today())));

//        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
//            @Override
//            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//
//                // here 날짜 눌렀을 때 !
//            }
//        }

        String[] activities = {"made a study room with Doe", "uploaded new activities"};
        ListView listView = (ListView) rootView.findViewById(R.id.profile_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, activities);
        listView.setAdapter(adapter);

        Button btn_my_post = rootView.findViewById(R.id.btn_myPost);
        btn_my_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProfileMyPost.class));
            }
        });

        return rootView;
    }

    /**
     * 유저의 기본 이미지에 대한 Bitmap 을 반환하는 작업 {ic_profile_foreground}
     *
     * @return 변환된 bitmap
     * @author Seunggun Sin
     * @since 2022-05-14
     */
    public Bitmap getDefaultProfileBitmap() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_profile_foreground);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 주 목적은 유저의 프로필 이미지를 갤러리에서 가져올 때, {@link TestUpdateProfile} 으로 이동하고
     * 변경 후, 다시 이 Fragment 로 되돌아 올 때 변경된 유저의 데이터를 갱신하기 위함
     * 즉, 이 Fragment 가 다시 실행되는 시점에서 유저 갱신 요청
     *
     * @author Seunggun Sin
     * @since 2022-05-14
     */
    @Override
    public void onResume() {
        super.onResume();
        Firestore.getUserData(User.currentUser.getUid())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // 요청 결과를 현재 유저 데이터에 전달
                            User.currentUser = task.getResult().toObject(User.class);
                            // 갱신한 유저 데이터에 대한 프로필 이미지를 다시 보여줌
                            Glide.with(getActivity())
                                    .load(User.currentUser.getProfileUrl())
                                    .fitCenter()
                                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(profileImage);
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setLevelProgress() {
        levelProgress.setProgress((int)(Math.round(
                (User.currentUser.getExp() * 1.0 - (int) LevelSystem.expCumulativeList[User.currentUser.getLevel()])
                        / ((int) LevelSystem.expCumulativeList[User.currentUser.getLevel() + 1] - (int) LevelSystem.expCumulativeList[User.currentUser.getLevel()])
                        * 100)));
        levelText.setText("Lv " + User.currentUser.getLevel() + " ("+User.currentUser.getExp()+"/"+LevelSystem.expCumulativeList[User.currentUser.getLevel() + 1]+")");
    }
}
