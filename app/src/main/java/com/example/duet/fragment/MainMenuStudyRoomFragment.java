package com.example.duet.fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.duet.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainMenuStudyRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuStudyRoomFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainMenuStudyRoomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainMenuStudyRoomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainMenuStudyRoomFragment newInstance(String param1, String param2) {
        MainMenuStudyRoomFragment fragment = new MainMenuStudyRoomFragment();
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
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_main_menu_study_room, container, false);

        ListView listview ;
        ChatRoomItemAdapter adapter;

        // Adapter 생성
        adapter = new ChatRoomItemAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) rootView.findViewById(R.id.chatroom_list);
        listview.setAdapter(adapter);

        // 첫 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_account_circle),
                "김원", "최근 대화 내용 어쩌고~") ;
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_account_circle),
                "정윤현", "최근 대화 내용 어쩌고~") ;
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_account_circle),
                "정옥란", "최근 대화 내용 어쩌고~") ;

        return rootView;
    }
}