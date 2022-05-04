package com.example.duet.util;

import com.example.duet.model.PostData;
import com.example.duet.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Firestore 관련 기능에 대해 짧은 참조로 사용하기 위한 클래스
 * - 모든 메소드들은 static 으로 통일해서 사용
 * - 각 메소드들을 호출하는 즉시 complete, success, failure 등 리스너로 상태 결과 획득
 * - Create, Read, Update, Delete 기능
 * - Read 의 경우 DocumentSnapshot 으로 결과 값이 메소드 리턴 타입의 Generic 으로 탑재
 * @author Seunggun Sin, 2022-05-01
 */
public class Firestore {

    /** Firestore 인스턴스 생성
     * ※ Firestore 클래스에서 Firestore 관련 CRUD 작업을 위한 인스턴스 생성
     * @return FirebaseFirestore 인스턴스
     */
    public static FirebaseFirestore getFirestoreInstance(){
        return FirebaseFirestore.getInstance();
    }

    /**
     * 새로운 유저를 Firestore 에 생성하는 요청
     * "user" collection → {uid} document 경로로 데이터 create
     * ※ User 모델의 나머지 attribute 들은 default 값으로 채워서 추가
     * @param uid 유저 고유 아이디
     * @param email 유저 이메일
     * @param nickname 유저 닉네임
     * @param userName 유저 이름
     * @return Task<Void>
     */
    public static Task<Void> createNewUser(String uid, String email, String nickname, String userName){
        return getFirestoreInstance().collection("user").document(uid).set(
                new User(uid, email, nickname, userName));
    }

    /**
     * Firestore 에 저장되어 있는 유저 데이터를 가져오는 요청
     * "user" collection → {uid} document 경로에서 데이터 read
     * @param uid 유저의 고유 아이디
     * @return Task<DocumentSnapshot>
     */
    public static Task<DocumentSnapshot> getUserData(String uid){
        return getFirestoreInstance().collection("user").document(uid).get();
    }

    public static Task<DocumentReference> createNewPost(PostData postData){
        return getFirestoreInstance().collection("post").add(postData);
    }
}
