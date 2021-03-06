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

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore 관련 기능에 대해 짧은 참조로 사용하기 위한 클래스
 * - 모든 메소드들은 static 으로 통일해서 사용
 * - 각 메소드들을 호출하는 즉시 complete, success, failure 등 리스너로 상태 결과 획득
 * - Create, Read, Update, Delete 기능
 * - Read 의 경우 DocumentSnapshot 으로 결과 값이 메소드 리턴 타입의 Generic 으로 탑재
 *
 * @author Seunggun Sin, 2022-05-01
 */
public class Firestore {

    /**
     * Firestore 인스턴스 생성
     * ※ Firestore 클래스에서 Firestore 관련 CRUD 작업을 위한 인스턴스 생성
     *
     * @return FirebaseFirestore 인스턴스
     */
    public static FirebaseFirestore getFirestoreInstance() {
        return FirebaseFirestore.getInstance();
    }

    /**
     * 새로운 유저를 Firestore 에 생성하는 요청
     * "user" collection → {uid} document 경로로 데이터 create
     * ※ User 모델의 나머지 attribute 들은 default 값으로 채워서 추가
     *
     * @param uid        유저 고유 아이디
     * @param email      유저 이메일
     * @param nickname   유저 닉네임
     * @param userName   유저 이름
     * @param profileUrl 유저 프로필 이미지 Url
     * @param token      유저 FCM 토큰
     * @return Task<Void>
     */
    public static Task<Void> createNewUser(String uid, String email, String nickname, String userName, String profileUrl, String token) {
        return getFirestoreInstance().collection("user").document(uid).set(
                new User(uid, email, nickname, userName, profileUrl, token));
    }

    /**
     * Firestore 에 저장되어 있는 유저 데이터를 가져오는 요청
     * "user" collection → {uid} document 경로에서 데이터 read
     *
     * @param uid 유저의 고유 아이디
     * @return Task<DocumentSnapshot>
     */
    public static Task<DocumentSnapshot> getUserData(String uid) {
        return getFirestoreInstance().collection("user").document(uid).get();
    }

    /**
     * 새로운 게시글 데이터를 생성하는 요청
     * "post" collection → {pid} document 경로로 데이터 create
     *
     * @param postData PostData instance
     * @return Task<DocumentReference>: 요청 결과
     */
    public static Task<DocumentReference> createNewPost(PostData postData) {
        return getFirestoreInstance().collection("post").add(postData);
    }

    /**
     * 새로운 게시글 데이터를 생성하고 난 뒤 비어있는 게시글 데이터의 ID 필드 값을 채워넣는 요청
     *
     * @param pid 새로 생성된 post id
     * @return Task<Void>: 요청 결과
     */
    public static Task<Void> insertPostId(String pid) {
        return getFirestoreInstance().collection("post").document(pid).update("postID", pid);
    }

    /**
     * Firestore 에 저장되어 있는 모든 게시글 데이터를 가져오는 요청
     * ※ 날짜별로 데이터 정렬(내림차순)
     * @return Task<QuerySnapshot> 게시글 데이터 집합
     */
    public static Task<QuerySnapshot> getAllPostData() {
        return getFirestoreInstance().collection("post").orderBy("writeDate", Query.Direction.DESCENDING).get();
    }

    /**
     * 게시글의 댓글을 저장하는 요청
     *
     * @param replyData ReplyData 객체 - 게시글 ID와 작성자 정보 포함
     * @return Task<DocumentReference>
     */
    public static Task<DocumentReference> addReplyData(ReplyData replyData) {
        return getFirestoreInstance().collection("reply").add(replyData);
    }

    /**
     * 게시글에 댓글을 달 때, reply 데이터의 replyID 필드에 아이디 값 저장하는 요청
     *
     * @param rid 저장할 게시글 ID
     * @return Task<Void>
     */
    public static Task<Void> insertReplyId(String rid) {
        return getFirestoreInstance().collection("reply").document(rid).update("replyID", rid);
    }

    /**
     * pid 에 해당하는 게시글에 있는 모든 reply 데이터를 가져오는 요청 - 게시글 주인에 대한 요청
     *
     * @param pid 게시글 아이디
     * @return Task<QuerySnapshot> 결과 값
     */
    public static Task<QuerySnapshot> getAllReplyOnPostForOwner(String pid) {
        return getFirestoreInstance().collection("reply").whereEqualTo("postIDtoReply", pid).get();
    }

    /**
     * pid 에 해당하는 게시글에 있는 모든 reply 데이터를 가져오는 요청 - 일반 사용자에 대한 요청
     *
     * @param pid 게시글 아이디
     * @return Task<QuerySnapshot> 결과 값
     */
    public static Task<QuerySnapshot> getAllReplyOnPostForAnybody(String pid) {
        return getFirestoreInstance().collection("reply").whereEqualTo("postIDtoReply", pid)
                .whereEqualTo("waiting", false).get();
    }

    /**
     * 댓글을 삭제하는 요청 - 댓글 작성자 본인 혹은 게시글 주인만 요청 가능
     *
     * @param rid 댓글 아이디
     * @return Task<Void>
     */
    public static Task<Void> removeReplyOnPostByOwner(String rid) {
        return getFirestoreInstance().collection("reply").document(rid).delete();
    }

    /**
     * 게시글의 댓글이 선택적 허용인 경우, 게시글 주인이 대기 중인 댓글을 승인했을 때 상태 변경을 요청하는 작업
     *
     * @param rid 상태 변경하고자하는 댓글 아이디
     * @return Task<Void>
     */
    public static Task<Void> updateReplyWaitingState(String rid) {
        Map<String, Object> toUpdateFields = new HashMap<>();
        toUpdateFields.put("waiting", false);
        toUpdateFields.put("viewType", 0);
        return getFirestoreInstance().collection("reply").document(rid).update(toUpdateFields);
    }

    /**
     * 게시글의 댓글이 게시글 주인으로부터 채택을 받았을 경우, 채택받은 상태를 변경하고자 하는 요청
     *
     * @param rid 상태 변경하고자 하는 댓글 아이디
     * @return Task<Void>
     */
    public static Task<Void> updateReplySelection(String rid) {
        return getFirestoreInstance().collection("reply").document(rid).update("selected", true);
    }

    /**
     * 유저의 프로필 이미지를 변경했을 때, 변경한 이미지에 대한 url 도 업데이트하는 요청
     *
     * @param uid 유저 아아디
     * @param url 변경된 이미지의 url
     * @return Task<Void>
     */
    public static Task<Void> updateUserProfileUrl(String uid, String url) {
        return getFirestoreInstance().collection("user").document(uid).update("profileUrl", url);
    }

    /**
     * parameter 에 해당하는 유저의 모든 게시글 데이터를 가져오는 요청
     *
     * @param uid 유저 아이디
     * @return Task<QuerySnapshot> 게시글 데이터들
     */
    public static Task<QuerySnapshot> getUserAllPostData(String uid) {
        return getFirestoreInstance().collection("post").whereEqualTo("writer.uid", uid).get();
    }

    /**
     * 유저의 신뢰도를 변경하는 요청
     *
     * @param positive 양수 음수 여부 {true: 2, false: -10}
     * @param uid      유저 아이디
     * @return Task<Void>
     */
    public static Task<Void> updateUserReliability(boolean positive, String uid) {
        int updateReliability = positive ? 2 : -10;
        return getFirestoreInstance().collection("user").document(uid).update("reliability", FieldValue.increment(updateReliability));
    }

    public static Task<Void> updateGroupLimitCount(String pid, int count) {
        return getFirestoreInstance().collection("post").document(pid).update("limitGroupCount", count);
    }

    public static Task<Void> updateUserInfoForReply(String rid, User user) {
        return getFirestoreInstance().collection("user").document(user.getUid()).set(user);
    }

    public static Task<Void> updateUserToken(String uid, String token) {
        return getFirestoreInstance().collection("user").document(uid).update("token", token);
    }

    public static Task<Void> updateUserPoint(String uid, int point) {
        return getFirestoreInstance().collection("user").document(uid).update("exp", FieldValue.increment(point));
    }

    public static Task<Void> updateUserLevel(String uid) {
        return getFirestoreInstance().collection("user").document(uid).update("level", FieldValue.increment(1));
    }

    public static Task<Void> updateUserExpForPosting(String uid) {
        return getFirestoreInstance().collection("user").document(uid).update("exp", FieldValue.increment(200));
    }

    public static Task<Void> updateUserExpForReply(String uid, boolean positive, int allocPoint) {
        return getFirestoreInstance().collection("user").document(uid).update("exp", FieldValue.increment(positive ? allocPoint : -1 * allocPoint));
    }
}
