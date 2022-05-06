package com.example.duet.util;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FireStorage {
    public static StorageReference postRef = getStorageInstance().getReference().child("post");
    public static StorageReference profileRef = getStorageInstance().getReference().child("userProfile");

    /**
     * FirebaseStorage 참조를 다루기 위한 인스턴스 생성 및 반환
     * @return FirebaseStorage instance
     */
    public static FirebaseStorage getStorageInstance(){
        return FirebaseStorage.getInstance();
    }

    /**
     * 게시글을 생성할 때 이미지를 저장해야하는데, 그 이미지를 Storage 에 업로드하는 작업
     * @param bitmap 업로드할 Bitmap 형식 이미지
     * @param uid 업로드 하는 주체의 uid
     * @return 저장 결과 (이미지가 저장된 url 정보 포함)
     * @author Seunggun Sin, 2022-05-06
     */
    public static Task<UploadTask.TaskSnapshot> uploadPostImage(Bitmap bitmap, String uid){
        StorageReference personalPostRef = postRef.child(uid+"/"+(System.currentTimeMillis())+".jpg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return personalPostRef.putBytes(data);
    }

    /**
     * 사용자의 프로필 이미지를 Storage에 업로드하는 작업
     * @param bitmap 업로드할 Bitmap 형식의 이미지
     * @param uid 업로드 하는 주체의 uid
     * @return 저장 결과 (이미지가 저장된 url 정보 포함)
     * @author Seunggun Sin, 2022-05-06
     */
    public static Task<UploadTask.TaskSnapshot> uploadUserProfileImage(Bitmap bitmap, String uid){
        // 항상 유저 프로필 이미지는 userProfileImage.jpg 로 저장
        StorageReference userRef = profileRef.child(uid+"/userProfileImage.jpg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return userRef.putBytes(data);
    }
}
