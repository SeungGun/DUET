package com.example.duet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.style.LineBackgroundSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.duet.board.PostContentActivity;
import com.example.duet.model.PostData;
import com.example.duet.model.User;
import com.example.duet.util.CustomProgressDialog;
import com.example.duet.util.Firestore;
import com.example.duet.util.LevelSystem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

public class UserProfileActivity extends AppCompatActivity {
    private User user;
    private ImageView profileImage;
    private TextView nicknameTextView;
    private TextView levelText;
    private TextView defensiveText;
    private ProgressBar levelProgress;
    private MaterialCalendarView materialCalendarView;
    private ArrayList<CalendarDay> calendars;
    private Map<CalendarDay, Integer> calendarDayIntegerMap;
    private ListView dayListView;
    private ArrayList<PostData> postDataArrayList;
    private ArrayList<String> selectedPostTitleList;
    private ArrayList<PostData> selectedPostDataList;
    private TextView selectDate;
    private CustomProgressDialog customProgressDialog;
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            UserProfileActivity.EventDecorator[] decorators = new UserProfileActivity.EventDecorator[8];
            for (int i = 0; i < decorators.length; ++i) {
                decorators[i] = new UserProfileActivity.EventDecorator(Color.RED, 6, i);
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
            for (int i = 0; i < postDataArrayList.size(); ++i) {
                Date cur = postDataArrayList.get(i).getWriteDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(cur);
                CalendarDay calendarDay = CalendarDay.from(calendar);
                if (calendarDay.equals(CalendarDay.today())) {
                    selectedPostTitleList.add(postDataArrayList.get(i).getTitle());
                    selectedPostDataList.add(postDataArrayList.get(i));
                }
            }
            selectDate.setText(CalendarDay.today().getYear() + "년 " + CalendarDay.today().getMonth() + "월 " + CalendarDay.today().getDay() + "일 활동");
            if(selectedPostTitleList.size() == 0){
                dayListView.setVisibility(View.GONE);
                defensiveText.setVisibility(View.VISIBLE);
            }
            else {
                dayListView.setVisibility(View.VISIBLE);
                defensiveText.setVisibility(View.GONE);
                ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, selectedPostTitleList);
                dayListView.setAdapter(stringArrayAdapter);
                dayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent contentIntent = new Intent(getApplicationContext(), PostContentActivity.class);
                        contentIntent.putExtra("data", selectedPostDataList.get(position));
                        startActivity(contentIntent);
                    }
                });
            }
            customProgressDialog.dismissDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        customProgressDialog = new CustomProgressDialog(UserProfileActivity.this);
        if (intent != null) {
            String uid = intent.getStringExtra("uid");
            customProgressDialog.showLoadingDialog();
            Firestore.getUserData(uid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        user = task.getResult().toObject(User.class);
                        setContentView(R.layout.activity_user_profile);
                        calendars = new ArrayList<>();
                        postDataArrayList = new ArrayList<>();
                        calendarDayIntegerMap = new HashMap<>();
                        selectedPostTitleList = new ArrayList<>();
                        selectedPostDataList = new ArrayList<>();
                        dayListView = findViewById(R.id.profile_list);
                        defensiveText = findViewById(R.id.defensive_text);
                        selectDate = findViewById(R.id.current_date_text);
                        materialCalendarView = findViewById(R.id.profile_calendar);
                        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
                        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                            @Override
                            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                                selectedPostTitleList.clear();
                                selectedPostDataList.clear();
                                for (int i = 0; i < postDataArrayList.size(); ++i) {
                                    Date cur = postDataArrayList.get(i).getWriteDate();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(cur);
                                    CalendarDay calendarDay = CalendarDay.from(calendar);
                                    if (calendarDay.equals(date)) {
                                        selectedPostTitleList.add(postDataArrayList.get(i).getTitle());
                                        selectedPostDataList.add(postDataArrayList.get(i));
                                    }
                                }
                                selectDate.setText(date.getYear() + "년 " + date.getMonth() + "월 " + date.getDay() + "일 활동");
                                if(selectedPostTitleList.size() == 0){
                                    dayListView.setVisibility(View.GONE);
                                    defensiveText.setVisibility(View.VISIBLE);
                                }
                                else {
                                    dayListView.setVisibility(View.VISIBLE);
                                    defensiveText.setVisibility(View.GONE);
                                    ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, selectedPostTitleList);
                                    dayListView.setAdapter(stringArrayAdapter);
                                    dayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent contentIntent = new Intent(getApplicationContext(), PostContentActivity.class);
                                            contentIntent.putExtra("data", selectedPostDataList.get(position));
                                            startActivity(contentIntent);
                                        }
                                    });
                                }
                            }
                        });
                        materialCalendarView.setSelectedDate(CalendarDay.today());


                        Firestore.getUserAllPostData(uid).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        PostData cur = documentSnapshot.toObject(PostData.class);
                                        postDataArrayList.add(cur);
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
                                    handler.sendEmptyMessage(0);
                                }
                            }
                        });
                        profileImage = findViewById(R.id.profile_photo);
                        nicknameTextView = findViewById(R.id.profile_nickname);
                        levelText = findViewById(R.id.profile_level);
                        levelProgress = findViewById(R.id.level_progress);
                        nicknameTextView.setText(user.getNickname());
                        levelText.setText("Lv " + user.getLevel() + " (" + user.getExp() + "/" + LevelSystem.expCumulativeList[user.getLevel() + 1] + ")");
                        levelProgress.setProgress((int) (Math.round(
                                (user.getExp() * 1.0 - (int) LevelSystem.expCumulativeList[user.getLevel()])
                                        / ((int) LevelSystem.expCumulativeList[user.getLevel() + 1] - (int) LevelSystem.expCumulativeList[user.getLevel()])
                                        * 100)));
                        Glide.with(getApplicationContext())
                                .load(user.getProfileUrl())
                                .fitCenter()
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(36)))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profileImage);
                    }
                }
            });
        }
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
            LineBackgroundSpan span = new CustomSpan(color, xOffsets[spanType]);
            view.addSpan(span);
        }
    }
}