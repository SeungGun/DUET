package com.example.duet.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.duet.R;
import com.example.duet.adapter.TestPostDataAdapter;
import com.example.duet.board.CreatePostActivity;
import com.example.duet.board.PostContentActivity;
import com.example.duet.model.PostData;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainMenuBulletinFragment extends Fragment {
    private FloatingActionButton createPostFab;
    private String[] items = {"코딩", "수학", "과학", "미술", "음악"};
    private ArrayList<PostData> postDataArrayList; // 게시글 전체 리스트
    private ArrayList<PostData> activityArrayList; // 자기계발 글 리스트
    private ArrayList<PostData> questionArrayList; // 질문 글 리스트
    /*
        위 3개의 ArrayList 를 이용하여 자기계발, 질문 구분 버튼을 통해 게시판 분리
     */
    private RecyclerView postRecyclerView;
    private TestPostDataAdapter adapter;
    private DividerItemDecoration dividerItemDecoration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu_bulletin, container, false);
        postDataArrayList = new ArrayList<>();
        activityArrayList = new ArrayList<>();
        questionArrayList = new ArrayList<>();
        postRecyclerView = rootView.findViewById(R.id.post_recyclerview);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        createPostFab = rootView.findViewById(R.id.fab_create_post);
        dividerItemDecoration = new DividerItemDecoration(postRecyclerView.getContext(), new LinearLayoutManager(getContext()).getOrientation());
        postRecyclerView.addItemDecoration(dividerItemDecoration);

        String[] items = {"수학", "과학", "프로그래밍", "미술", "음악", "국어"};
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String item = String.valueOf(adapterView.getItemAtPosition(i));
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        getAllPostDataAndSetting();
        createPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreatePostActivity.class));
            }
        });
        return rootView;
    }

    private void getAllPostDataAndSetting() {
        Firestore.getAllPostData().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    postDataArrayList.clear();
                    questionArrayList.clear();
                    activityArrayList.clear();
                    int i=0;
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        postDataArrayList.add(documentSnapshot.toObject(PostData.class));
                        if(postDataArrayList.get(i).getPostType() == 1){ // 게시글 유형이 질문 글이라면
                            questionArrayList.add(postDataArrayList.get(i)); // 게시글 데이터를 질문 list 에 추가
                        }
                        else{
                            activityArrayList.add(postDataArrayList.get(i)); // 게시글 데이터를 자기계발 list 에 추가
                        }
                        i++;
                    }
                    adapter = new TestPostDataAdapter(activityArrayList, getContext());
                    adapter.setOnItemClickListener(new TestPostDataAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClicked(int position, PostData data) {
                            Intent intent = new Intent(getContext(), PostContentActivity.class);
                            intent.putExtra("position", position);
                            intent.putExtra("data", data);
                            startActivity(intent);
                        }
                    });
                    postRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllPostDataAndSetting();
    }
}