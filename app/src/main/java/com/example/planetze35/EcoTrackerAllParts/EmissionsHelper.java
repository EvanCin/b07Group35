package com.example.planetze35.EcoTrackerAllParts;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmissionsHelper {

    // Method to update total emissions for a specific date in Firebase
    public static void updateTotalEmissions(DatabaseReference dailyActivitiesRef) {
        dailyActivitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double totalCo2e = 0;

                    // Loop through categories and recalculate emissions
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot subCategorySnapshot : categorySnapshot.getChildren()) {
                            // Check CO2e in subcategory
                            if (subCategorySnapshot.hasChild("CO2e")) {
                                Double co2eValue = subCategorySnapshot.child("CO2e").getValue(Double.class);
                                if (co2eValue != null) {
                                    totalCo2e += co2eValue;
                                }
                            } else {
                                // Check in sub-subcategories as well
                                for (DataSnapshot subSubCategorySnapshot : subCategorySnapshot.getChildren()) {
                                    if (subSubCategorySnapshot.hasChild("CO2e")) {
                                        Double co2eValue = subSubCategorySnapshot.child("CO2e").getValue(Double.class);
                                        if (co2eValue != null) {
                                            totalCo2e += co2eValue;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // After recalculation, update the total emissions in Firebase
                    dailyActivitiesRef.child("total_daily_emissions").setValue(totalCo2e);
                    Log.d("EmissionsHelper", "Total emissions updated: " + totalCo2e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log error if Firebase operation fails
                Log.e("EmissionsHelper", "Database operation failed: " + databaseError.getMessage());
            }
        });
    }

    // Method to remove an activity from Firebase
    public static void removeActivityFromFirebase(DatabaseReference dailyActivitiesRef, String activityName) {
        dailyActivitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot subCategorySnapshot : categorySnapshot.getChildren()) {
                            if (subCategorySnapshot.getKey().equals(activityName)) {
                                // Log activity removal for debugging
                                Log.d("EmissionsHelper", "Removing activity: " + activityName);
                                subCategorySnapshot.getRef().removeValue();  // Remove the activity
                                break;  // Break after removing the activity
                            } else {
                                // Check in sub-subcategories as well
                                for (DataSnapshot subSubCategorySnapshot : subCategorySnapshot.getChildren()) {
                                    if (subSubCategorySnapshot.getKey().equals(activityName)) {
                                        // Log activity removal for debugging
                                        Log.d("EmissionsHelper", "Removing activity: " + activityName);
                                        subSubCategorySnapshot.getRef().removeValue();  // Remove the activity
                                        break;  // Break after removing the activity
                                    }
                                }
                            }
                        }
                    }

                    // After removal, update the total emissions
                    updateTotalEmissions(dailyActivitiesRef);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log error if Firebase operation fails
                Log.e("EmissionsHelper", "Failed to remove activity: " + databaseError.getMessage());
            }
        });
    }
}
