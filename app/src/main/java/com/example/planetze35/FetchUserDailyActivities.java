package com.example.planetze35;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class FetchUserDailyActivities {

    public void fetchUserActivities(String userId, RecommendationsActivity.FirebaseCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("DailyActivities");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> categoryCounts = new HashMap<>();
                boolean hasValidCategory = false;

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot categorySnapshot : dateSnapshot.getChildren()) {
                        String category = categorySnapshot.getKey();
                        if (category != null) {
                            categoryCounts.merge(category, 1, Integer::sum);
                            hasValidCategory = true;
                        }
                    }
                }

                if (hasValidCategory) {
                    callback.onDataFetched(categoryCounts);
                } else {
                    callback.onError(new Exception("No valid categories found."));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }
}
