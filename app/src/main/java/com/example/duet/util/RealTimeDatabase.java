package com.example.duet.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealTimeDatabase {
    public static FirebaseDatabase database;

    public static DatabaseReference getDatabaseRef() {
        //setEmulator();
        database = FirebaseDatabase.getInstance();
        return database.getReference();
    }

    public static void setEmulator() {
        database.useEmulator("10.0.2.2", 9000);
    }
}
