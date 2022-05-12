package com.example.duet.util;

import com.example.duet.model.PostData;
import com.example.duet.model.ReplyData;
import com.example.duet.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
     * @param profileUrl 유저 프로필 이미지 Url
     * @return Task<Void>
     */
    public static Task<Void> createNewUser(String uid, String email, String nickname, String userName, String profileUrl){
        return getFirestoreInstance().collection("user").document(uid).set(
                new User(uid, email, nickname, userName, profileUrl));
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

    /**
     * 새로운 게시글 데이터를 생성하는 요청
     * "post" collection → {pid} document 경로로 데이터 create
     * @param postData PostData instance
     * @return Task<DocumentReference>: 요청 결과
     */
    public static Task<DocumentReference> createNewPost(PostData postData){
        return getFirestoreInstance().collection("post").add(postData);
    }

    /**
     * 새로운 게시글 데이터를 생성하고 난 뒤 비어있는 게시글 데이터의 ID 필드 값을 채워넣는 요청
     * @param pid 새로 생성된 post id
     * @return Task<Void>: 요청 결과
     */
    public static Task<Void> insertPostId(String pid){
        return getFirestoreInstance().collection("post").document(pid).update("postID", pid);
    }

    /**
     * Firestore 에 저장되어 있는 모든 게시글 데이터를 가져오는 요청
     * @return Task<QuerySnapshot> 게시글 데이터 집합
     */
    public static Task<QuerySnapshot> getAllPostData(){
        return getFirestoreInstance().collection("post").get();
    }

    /**
     * 게시글의 댓글을 저장하는 요청
     * @param replyData ReplyData 객체 - 게시글 ID와 작성자 정보 포함
     * @return Task<DocumentReference>
     */
    public static Task<DocumentReference> addReplyData(ReplyData replyData){
        return getFirestoreInstance().collection("reply").add(replyData);
    }

    /**
     * 게시글에 댓글을 달 때, reply 데이터의 replyID 필드에 아이디 값 저장하는 요청
     * @param rid 저장할 게시글 ID
     * @return Task<Void>
     */
    public static Task<Void> insertReplyId(String rid){
        return getFirestoreInstance().collection("reply").document(rid).update("replyID", rid);
    }

    /**
     * pid 에 해당하는 게시글에 있는 모든 reply 데이터를 가져오는 요청
     * @param pid 게시글 아이디
     * @return Tak<QuerySnapshot> 결과 값
     */
    public static Task<QuerySnapshot> getAllReplyOnPostForOwner(String pid){
        return getFirestoreInstance().collection("reply").whereEqualTo("postIDtoReply", pid).get();
    }

    public static Task<QuerySnapshot> getAllReplyOnPostForAnybody(String pid){
        return getFirestoreInstance().collection("reply").whereEqualTo("postIDtoReply", pid).whereEqualTo("isWaiting", false).get();
    }

    public static Task<Void> updateUserPoint(String uid, int point){
        return getFirestoreInstance().collection("user").document(uid).update("exp", FieldValue.increment(point));
    }

    public static Task<Void> updateUserExpForPosting(String uid){
        return getFirestoreInstance().collection("user").document(uid).update("exp", FieldValue.increment(100));
    }

    public static Task<QuerySnapshot> getUserAllPostData(String uid){
        return getFirestoreInstance().collection("post").whereEqualTo("writerID", uid).get();
    }

    public static Task<Void> updateUserReliability(boolean positive, String uid){
        int updateReliability = positive ? 2 : -10;
        return getFirestoreInstance().collection("user").document(uid).update("reliability", FieldValue.increment(updateReliability));
    }
}
