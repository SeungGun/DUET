package com.example.duet.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.duet.R;
import com.example.duet.board.TestUpdateProfile;
import com.example.duet.model.User;
import com.example.duet.util.FireStorage;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.example.duet.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainMenuProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView profileImage;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Firestore.getUserData(User.currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        User.currentUser = task.getResult().toObject(User.class);
                        Glide.with(getActivity())
                                .load(User.currentUser.getProfileUrl())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profileImage);
                    }
                }
            });
        }
    };
    public MainMenuProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainMenuProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMenuProfileFragment newInstance(String param1, String param2) {
        MainMenuProfileFragment fragment = new MainMenuProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("fragment", "onCreateView");
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_main_menu_profile, container, false);
        profileImage = rootView.findViewById(R.id.profile_photo);
        profileImage.setScaleType(ImageView.ScaleType.FIT_XY); // 깔끔한 공간과 비율을 위해 필요
        profileImage.setAdjustViewBounds(true); // 깔끔한 공간과 비율을 위해 필요

        Glide.with(getActivity())
                .load(User.currentUser.getProfileUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("프로필 변경");
                String[] items = {"갤러리에서 가져오기", "기본 이미지로 변경"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            startActivity(new Intent(getActivity(), TestUpdateProfile.class));
                        }
                        else{
                            FireStorage.uploadUserProfileImage(getDefaultProfileBitmap(), User.currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        task.getResult().getStorage().getDownloadUrl()
                                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Uri> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(getActivity(), "이미지 업데이트 성공", Toast.LENGTH_SHORT).show();
                                                            Firestore.updateUserProfileUrl(User.currentUser.getUid()
                                                                    , task.getResult().toString())
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
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

        String[] activities = {"made a study room with 정윤현", "uploaded new activities"};
        ListView listView = (ListView) rootView.findViewById(R.id.profile_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, activities);
        listView.setAdapter(adapter);


        return rootView;
    }

    public Bitmap getDefaultProfileBitmap(){
        Drawable drawable = getResources().getDrawable(R.drawable.ic_profile_foreground);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void onResume() {
        super.onResume();
        Firestore.getUserData(User.currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    User.currentUser = task.getResult().toObject(User.class);
                    Glide.with(getActivity())
                            .load(User.currentUser.getProfileUrl())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(profileImage);
                }
            }
        });
    }
}