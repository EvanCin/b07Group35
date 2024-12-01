package com.example.planetze35.data;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.Map;

public class UserDataHandler {

    private final DatabaseReference databaseReference;

    public UserDataHandler() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void fetchUserData(String userId, UserDataCallback callback) {
        DatabaseReference userRef = databaseReference.child("users").child(userId).child("habits");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Integer> categoryCounts = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.child("category").getValue(String.class);

                    if (category != null) {
                        categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
                    }
                }

                callback.onDataFetched(categoryCounts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public interface UserDataCallback {
        void onDataFetched(Map<String, Integer> categoryCounts);
        void onError(Exception e);
    }
}
