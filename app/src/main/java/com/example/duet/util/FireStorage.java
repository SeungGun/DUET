package com.example.duet.util;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FireStorage {
    public static StorageReference postRef = getStorageInstance().getReference().child("post");

    public static FirebaseStorage getStorageInstance(){
        return FirebaseStorage.getInstance();
    }

    public static Task<UploadTask.TaskSnapshot> uploadPostImage(Bitmap bitmap, String uid){
        StorageReference personalPostRef = postRef.child(uid+"/"+(System.currentTimeMillis())+".jpg");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return personalPostRef.putBytes(data);
    }
}
