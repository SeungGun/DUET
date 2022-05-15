package com.example.duet.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.duet.R;
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

    private BulletinPost fragmentPost = new BulletinPost();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_main_menu_profile, container, false);

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
}