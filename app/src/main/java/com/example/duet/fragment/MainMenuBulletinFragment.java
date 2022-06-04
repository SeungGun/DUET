package com.example.duet.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.duet.R;
import com.example.duet.adapter.PostDataAdapter;
import com.example.duet.board.CreatePostActivity;
import com.example.duet.board.PostContentActivity;
import com.example.duet.model.PostData;
import com.example.duet.util.Firestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private PostDataAdapter adapter;
    private DividerItemDecoration dividerItemDecoration;
    private Chip postType;
    private Chip questionType;
    private Chip categoryMath;
    private Chip categoryScience;
    private Chip categoryProgramming;
    private Chip categoryArt;
    private Chip categoryKorean;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<PostData> selectArrayList;
    private boolean isInitial = true;
    private ArrayList<String> currentSelectedCategoryList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu_bulletin, container, false);
        postDataArrayList = new ArrayList<>();
        activityArrayList = new ArrayList<>();
        questionArrayList = new ArrayList<>();
        selectArrayList = new ArrayList<>();
        currentSelectedCategoryList = new ArrayList<>();
        postType = rootView.findViewById(R.id.sefChip);
        categoryMath = rootView.findViewById(R.id.mathChip);
        swipeRefreshLayout = rootView.findViewById(R.id.pullToRefresh);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO 새로운 게시글 가져오기
                swipeRefreshLayout.setRefreshing(false);
            }


        });



        categoryMath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String title = "수학";
                if (isChecked) {
                    currentSelectedCategoryList.add(title);
                } else {
                    currentSelectedCategoryList.remove(title);
                }
                changeTagState();
            }
        });
        categoryScience = rootView.findViewById(R.id.scienceChip);

        categoryScience.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String title = "과학";
                if (isChecked) {
                    currentSelectedCategoryList.add(title);
                } else {
                    currentSelectedCategoryList.remove(title);
                }
                changeTagState();
            }
        });
        categoryProgramming = rootView.findViewById(R.id.programChip);
        categoryProgramming.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String title = "프로그래밍";
                if (isChecked) {
                    currentSelectedCategoryList.add(title);
                } else {
                    currentSelectedCategoryList.remove(title);
                }
                changeTagState();
            }
        });
        categoryArt = rootView.findViewById(R.id.artChip);
        categoryArt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String title = "미술";
                if (isChecked) {
                    currentSelectedCategoryList.add(title);
                } else {
                    currentSelectedCategoryList.remove(title);
                }
                changeTagState();
            }
        });
        categoryKorean = rootView.findViewById(R.id.koreanChip);
        categoryKorean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String title = "국어";
                if (isChecked) {
                    currentSelectedCategoryList.add(title);
                } else {
                    currentSelectedCategoryList.remove(title);
                }
                changeTagState();
            }
        });
        questionType = rootView.findViewById(R.id.questionChip);
        questionType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    postType.setChecked(false);
                    adapter = new PostDataAdapter(questionArrayList, getContext());
                    adapter.setOnItemClickListener(new PostDataAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClicked(int position, PostData data) {
                            Intent intent = new Intent(getContext(), PostContentActivity.class);
                            intent.putExtra("position", position);
                            intent.putExtra("data", data);
                            startActivity(intent);
                        }
                    });
                    postRecyclerView.setAdapter(adapter);
                } else {
                    postType.setChecked(true);
                }
                changeTagState();
            }
        });
        postType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    questionType.setChecked(false);
                    adapter = new PostDataAdapter(activityArrayList, getContext());
                    adapter.setOnItemClickListener(new PostDataAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClicked(int position, PostData data) {
                            Intent intent = new Intent(getContext(), PostContentActivity.class);
                            intent.putExtra("position", position);
                            intent.putExtra("data", data);
                            startActivity(intent);
                        }
                    });
                    postRecyclerView.setAdapter(adapter);
                } else {
                    questionType.setChecked(true);
                }
                changeTagState();
            }
        });
        postRecyclerView = rootView.findViewById(R.id.post_recyclerview);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        createPostFab = rootView.findViewById(R.id.fab_create_post);
        dividerItemDecoration = new DividerItemDecoration(postRecyclerView.getContext(), new LinearLayoutManager(getContext()).getOrientation());
        postRecyclerView.addItemDecoration(dividerItemDecoration);

        isInitial = true;
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
                    if (isInitial) {
                        int i = 0;
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            postDataArrayList.add(documentSnapshot.toObject(PostData.class));

                            if (postDataArrayList.get(i).getPostType() == 1) { // 게시글 유형이 질문 글이라면
                                questionArrayList.add(postDataArrayList.get(i)); // 게시글 데이터를 질문 list 에 추가
                            } else {
                                activityArrayList.add(postDataArrayList.get(i)); // 게시글 데이터를 자기계발 list 에 추가
                            }
                            i++;
                        }
                        adapter = new PostDataAdapter(activityArrayList, getContext());
                        adapter.setOnItemClickListener(new PostDataAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClicked(int position, PostData data) {
                                Intent intent = new Intent(getContext(), PostContentActivity.class);
                                intent.putExtra("position", position);
                                intent.putExtra("data", data);
                                startActivity(intent);
                            }
                        });
                        postRecyclerView.setAdapter(adapter);
                        isInitial = false;
                    } else {
                        int currentSize = activityArrayList.size() + questionArrayList.size();
                        int i = 0;
                        PostData init = null;
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if (i == 0) {
                                init = documentSnapshot.toObject(PostData.class);
                            }
                            i++;
                        }
                        if (currentSize < i) {
                            postDataArrayList.add(init);
                            if (init.getPostType() == 1) {
                                questionArrayList.add(0, init);
                                adapter = new PostDataAdapter(questionArrayList, getContext());
                            } else {
                                activityArrayList.add(0, init);
                                adapter = new PostDataAdapter(activityArrayList, getContext());
                            }
                            adapter.setOnItemClickListener(new PostDataAdapter.OnItemClickListener() {
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
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllPostDataAndSetting();
    }

    /**
     * 카테고리에 해당하는 Chip 클릭 시 호출, 현재 선택된 카테고리들과 자기계발 or 질문에 따른 게시글 분류해서 보여주는 작업
     */
    public void changeTagState() {
        selectArrayList.clear();
        if (questionType.isChecked()) {
            for (int i = 0; i < questionArrayList.size(); ++i) {
                boolean isAddable = true;
                for (int j = 0; j < currentSelectedCategoryList.size(); ++j) {
                    if (!questionArrayList.get(i).getCategory().contains(currentSelectedCategoryList.get(j))) {
                        isAddable = false;
                        break;
                    }
                }
                if (isAddable) {
                    selectArrayList.add(questionArrayList.get(i));
                }
            }
            adapter = new PostDataAdapter(selectArrayList, getContext());
            postRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new PostDataAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(int position, PostData data) {
                    Intent intent = new Intent(getContext(), PostContentActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("data", data);
                    startActivity(intent);
                }
            });
        } else {
            for (int i = 0; i < activityArrayList.size(); ++i) {
                boolean isAddable = true;
                for (int j = 0; j < currentSelectedCategoryList.size(); ++j) {
                    if (!activityArrayList.get(i).getCategory().contains(currentSelectedCategoryList.get(j))) {
                        isAddable = false;
                        break;
                    }
                }
                if (isAddable) {
                    selectArrayList.add(activityArrayList.get(i));
                }
            }
            adapter = new PostDataAdapter(selectArrayList, getContext());
            postRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new PostDataAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(int position, PostData data) {
                    Intent intent = new Intent(getContext(), PostContentActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("data", data);
                    startActivity(intent);
                }
            });
        }

    }
}