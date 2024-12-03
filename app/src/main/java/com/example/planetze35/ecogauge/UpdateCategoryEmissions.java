package com.example.planetze35.ecogauge;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateCategoryEmissions {
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private String userId;

    public UpdateCategoryEmissions(String userId) {
        this.userId = userId;
        database = FirebaseDatabase.getInstance();
        //userRef = database.getReference("users").child(userId);
        //TODO: replace with actual userid
        userRef = database.getReference("users/defaultUserId");
    }

    public void updateCategories() {
        userRef.child("DailyActivities").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Initialize total variables
                double totalTransportationEmissions = 0;
                double totalEnergyEmissions = 0;
                double totalConsumptionEmissions = 0;

                // Check if DailyActivities exist
                if (dataSnapshot.exists()) {
                    // Iterate through all dates under DailyActivities
                    for (DataSnapshot currSnapShot : dataSnapshot.getChildren()) {
                        if (currSnapShot.hasChild("transportation")) {
                            DataSnapshot transportationSnapshot = currSnapShot.child("transportation");
                            totalTransportationEmissions += getCategoryCO2(transportationSnapshot);
                        }
                        if (currSnapShot.hasChild("food")) {
                            DataSnapshot foodSnapshot = currSnapShot.child("food");
                            totalConsumptionEmissions += getCategoryCO2(foodSnapshot);
                        }
                        if (currSnapShot.hasChild("consumption")) {
                            DataSnapshot consumptionSnapshot = currSnapShot.child("consumption");
                            totalConsumptionEmissions += getCategoryCO2(consumptionSnapshot);
                        }
                        if (currSnapShot.hasChild("energyBills")) {
                            DataSnapshot energySnapshot = currSnapShot.child("energyBills");
                            totalEnergyEmissions += getCategoryCO2(energySnapshot);
                        }
                    }
                    updateTotalsInFirebase(totalTransportationEmissions, totalEnergyEmissions, totalConsumptionEmissions);
                } else {
                    // If DailyActivities doesn't exist, set all totals to 0
                    updateTotalsInFirebase(0, 0, 0);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UpdateCategoryEmissions","databaseError");
            }
        });
    }

    private double getCategoryCO2(DataSnapshot categorySnapshot) {
        double categoryCO2 = 0;
        // Loop through all subcategories in the category
        for (DataSnapshot subCategorySnapshot : categorySnapshot.getChildren()) {
            if (subCategorySnapshot.hasChild("CO2e")) {
                Double co2Value = subCategorySnapshot.child("CO2e").getValue(Double.class);
                if (co2Value != null) {
                    categoryCO2 += co2Value;
                }
            } else {
                for (DataSnapshot subSubCategorySnapshot : subCategorySnapshot.getChildren()) {
                    Double co2Value = subSubCategorySnapshot.child("CO2e").getValue(Double.class);
                    if (co2Value != null) {
                        categoryCO2 += co2Value;
                    }
                }
            }
        }
        return categoryCO2;
    }

    private void updateTotalsInFirebase(double totalTransportationEmissions, double totalEnergyEmissions, double totalConsumptionEmissions) {
        userRef.child("transportationEmissions").setValue(totalTransportationEmissions);
        userRef.child("energyEmissions").setValue(totalEnergyEmissions);
        userRef.child("consumptionEmissions").setValue(totalConsumptionEmissions);
    }
}