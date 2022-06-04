package com.example.duet.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.text.style.LineBackgroundSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.duet.board.PostContentActivity;
import com.example.duet.R;
import com.example.duet.board.UpdateProfile;
import com.example.duet.model.PostData;
import com.example.duet.model.User;
import com.example.duet.util.CustomProgressDialog;
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
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MainMenuProfileFragment extends Fragment {

    private ImageView profileImage;
    private ArrayList<PostData> userPostList;
    private ProgressBar levelProgress;
    private TextView levelText;
    private TextView nicknameText;
    private MaterialCalendarView materialCalendarView;
    private ArrayList<CalendarDay> calendars;
    private Map<CalendarDay, Integer> calendarDayIntegerMap;
    private ListView dayListView;
    private ArrayList<String> selectedPostTitleList;
    private ArrayList<PostData> selectedPostDataList;
    private TextView selectDate;
    private TextView defensiveText;
    private CustomProgressDialog customProgressDialog;
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.arg1 == 1) {
                MainMenuProfileFragment.EventDecorator[] decorators = new MainMenuProfileFragment.EventDecorator[8];
                for (int i = 0; i < decorators.length; ++i) {
                    decorators[i] = new MainMenuProfileFragment.EventDecorator(Color.RED, 6, i);
                }

                for (Map.Entry<CalendarDay, Integer> entry : calendarDayIntegerMap.entrySet()) {
                    CalendarDay cur = entry.getKey();
                    Integer curCount = entry.getValue();
                    for (int i = 0; i < (curCount > 8 ? 8 : curCount); ++i) {
                        decorators[i].addDate(cur);
                    }
                }
                materialCalendarView.addDecorators(decorators);
                selectedPostTitleList.clear();
                for (int i = 0; i < userPostList.size(); ++i) {
                    Date cur = userPostList.get(i).getWriteDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(cur);
                    CalendarDay calendarDay = CalendarDay.from(calendar);
                    if (calendarDay.equals(CalendarDay.today())) {
                        selectedPostTitleList.add(userPostList.get(i).getTitle());
                        selectedPostDataList.add(userPostList.get(i));
                    }
                }
                selectDate.setText(CalendarDay.today().getYear() + "년 " + CalendarDay.today().getMonth() + "월 " + CalendarDay.today().getDay() + "일 활동");
                if (selectedPostTitleList.size() == 0) {
                    dayListView.setVisibility(View.GONE);
                    defensiveText.setVisibility(View.VISIBLE);
                } else {
                    dayListView.setVisibility(View.VISIBLE);
                    defensiveText.setVisibility(View.GONE);
                    ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, selectedPostTitleList);
                    dayListView.setAdapter(stringArrayAdapter);
                    dayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent contentIntent = new Intent(getActivity(), PostContentActivity.class);
                            contentIntent.putExtra("data", selectedPostDataList.get(position));
                            startActivity(contentIntent);
                        }
                    });
                }
                customProgressDialog.dismissDialog();
                return;
            }
            if (msg.arg1 == 2) {
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
                                            .fitCenter()
                                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(24)))
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(profileImage);
                                }
                            }
                        });
            }
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

        customProgressDialog = new CustomProgressDialog(getActivity());
        profileImage = rootView.findViewById(R.id.profile_photo);
        userPostList = new ArrayList<>();
        profileImage.setScaleType(ImageView.ScaleType.FIT_XY); // 깔끔한 공간과 비율을 위해 필요
        profileImage.setAdjustViewBounds(true); // 깔끔한 공간과 비율을 위해 필요
        levelProgress = rootView.findViewById(R.id.level_progress);
        levelText = rootView.findViewById(R.id.profile_level);
        nicknameText = rootView.findViewById(R.id.profile_nickname);
        defensiveText = rootView.findViewById(R.id.defensive_text);
        nicknameText.setText(User.currentUser.getNickname());
        calendars = new ArrayList<>();
        calendarDayIntegerMap = new HashMap<>();
        selectedPostTitleList = new ArrayList<>();
        selectedPostDataList = new ArrayList<>();
        dayListView = rootView.findViewById(R.id.profile_list);
        selectDate = rootView.findViewById(R.id.current_date_text);
        materialCalendarView = rootView.findViewById(R.id.profile_calendar);
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedPostTitleList.clear();
                selectedPostDataList.clear();
                for (int i = 0; i < userPostList.size(); ++i) {
                    Date cur = userPostList.get(i).getWriteDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(cur);
                    CalendarDay calendarDay = CalendarDay.from(calendar);
                    if (calendarDay.equals(date)) {
                        selectedPostTitleList.add(userPostList.get(i).getTitle());
                        selectedPostDataList.add(userPostList.get(i));
                    }
                }
                selectDate.setText(date.getYear() + "년 " + date.getMonth() + "월 " + date.getDay() + "일 활동");
                if (selectedPostTitleList.size() == 0) {
                    dayListView.setVisibility(View.GONE);
                    defensiveText.setVisibility(View.VISIBLE);
                } else {
                    dayListView.setVisibility(View.VISIBLE);
                    defensiveText.setVisibility(View.GONE);
                    ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, selectedPostTitleList);
                    dayListView.setAdapter(stringArrayAdapter);
                    dayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent contentIntent = new Intent(getActivity(), PostContentActivity.class);
                            contentIntent.putExtra("data", selectedPostDataList.get(position));
                            startActivity(contentIntent);
                        }
                    });
                }
            }
        });
        materialCalendarView.setSelectedDate(CalendarDay.today());

        setLevelProgress();
        /*
         현재 사용자의 모든 게시글 데이터를 가져오는 요청 {userPostList 이름의 ArrayList 에 PostData 저장}
         */
        customProgressDialog.showLoadingDialog();
        Firestore.getUserAllPostData(User.currentUser.getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        PostData cur = documentSnapshot.toObject(PostData.class);
                        userPostList.add(cur);
                        Date date = cur.getWriteDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        CalendarDay calendarDay = CalendarDay.from(calendar);
                        calendars.add(calendarDay);
                        if (calendarDayIntegerMap.containsKey(calendarDay)) {
                            calendarDayIntegerMap.put(calendarDay, calendarDayIntegerMap.get(calendarDay) + 1);
                        } else {
                            calendarDayIntegerMap.put(calendarDay, 1);
                        }
                    }
                    Message message = handler.obtainMessage();
                    message.arg1 = 1;
                    handler.sendMessage(message);
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
                            startActivity(new Intent(getActivity(), UpdateProfile.class));
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
                                                                                        message.arg1 = 2;
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
     * 주 목적은 유저의 프로필 이미지를 갤러리에서 가져올 때, {@link UpdateProfile} 으로 이동하고
     * 변경 후, 다시 이 Fragment 로 되돌아 올 때 변경된 유저의 데이터를 갱신하기 위함
     * 즉, 이 Fragment 가 다시 실행되는 시점에서 유저 갱신 요청
     *
     * @author Seunggun Sin
     * @since 2022-05-14
     */
    @Override
    public void onResume() {
        super.onResume();
        materialCalendarView.setSelectedDate(CalendarDay.today());
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
        levelProgress.setProgress((int) (Math.round(
                (User.currentUser.getExp() * 1.0 - (int) LevelSystem.expCumulativeList[User.currentUser.getLevel()])
                        / ((int) LevelSystem.expCumulativeList[User.currentUser.getLevel() + 1] - (int) LevelSystem.expCumulativeList[User.currentUser.getLevel()])
                        * 100)));
        levelText.setText("Lv " + User.currentUser.getLevel() + " (" + User.currentUser.getExp() + "/" + LevelSystem.expCumulativeList[User.currentUser.getLevel() + 1] + ")");
    }

    private static class CustomSpan extends DotSpan {
        private int color;
        private int xOffset;
        private float radius = 4;

        CustomSpan(int color, int xOffset) {
            this.color = color;
            this.xOffset = xOffset;
        }

        @Override
        public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence charSequence, int start, int end, int lineNum) {
            int oldColor = paint.getColor();
            if (color != 0) {
                paint.setColor(color);
            }
            int x = ((left + right) / 2);

            canvas.drawCircle(x + xOffset, bottom + radius, radius, paint);
            paint.setColor(oldColor);
        }
    }

    private static class EventDecorator implements DayViewDecorator {
        private static final int[] xOffsets = new int[]{0, -10, 10, -20, 20, -30, 30, -40};
        private int color;
        private HashSet<CalendarDay> dates;
        private float dotRadius;
        private int spanType;

        public EventDecorator(int color, float dotRadius, int spanType) {
            this.color = color;
            this.dotRadius = dotRadius;
            this.spanType = spanType;
            this.dates = new HashSet<>();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        public boolean addDate(CalendarDay day) {
            return dates.add(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            LineBackgroundSpan span = new MainMenuProfileFragment.CustomSpan(color, xOffsets[spanType]);
            view.addSpan(span);
        }
    }
}
