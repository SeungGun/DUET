package com.example.duet.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.duet.R;
import com.example.duet.adapter.CardAdapter;
import com.example.duet.model.CardData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    ChildEventListener mChildEventListener;
    CardAdapter mAdapter;
    ArrayList<CardData> cardDataArrayList;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

        ListView listView = (ListView) rootView.findViewById(R.id.listView);

        cardDataArrayList = new ArrayList<>();
        mAdapter = new CardAdapter(rootView.getContext(), cardDataArrayList);
        listView.setAdapter(mAdapter);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        recvChatRoom();

    }

    @Override
    public void onPause() {
        super.onPause();
        cardDataArrayList.clear();
        mChildEventListener = null;
    }

    private void recvChatRoom() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    Map<String, Boolean> member = new HashMap<String, Boolean>();
                    Boolean AmIIn = false;

                    for (DataSnapshot ds: snapshot.getChildren()) {


                        if (ds.getKey().equals("members")) {
                            for (DataSnapshot dsMember: ds.getChildren()) {
                                if(dsMember.getKey().equals(mAuth.getUid())) {
                                    AmIIn = true;
                                }
                            }

                            Log.d("user", member.toString());
                        }

                        if(AmIIn) {
                            if (ds.getKey().equals("user_names")) {
                                for (DataSnapshot dsMember: ds.getChildren()) {
                                    Log.d("asd", dsMember.getValue().toString());
                                    member.put(dsMember.getKey(), dsMember.getValue(Boolean.class));
                                }

                                Log.d("user", member.toString());
                            }
                        }
                    }

                    if (AmIIn) {
                        CardData cd = snapshot.getValue(CardData.class);
                        cd.setMembers(member);
                        cd.setConvKey(snapshot.getKey());
                        cardDataArrayList.add(cd);
                        mAdapter.notifyDataSetChanged();
                    }


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            };
            mRef.child("chat_meta").addChildEventListener(mChildEventListener);

        }

    }
}