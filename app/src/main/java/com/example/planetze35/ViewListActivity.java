package com.example.planetze35;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewListActivity extends AppCompatActivity implements ActivityAdapterWithButtons.ActivityClickListener {

    private RecyclerView recyclerView;
    private ActivityAdapterWithButtons adapter;
    private List<ActivityItem> activityList;
    private String selectedDate;
    private DatabaseReference dailyActivitiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the selected date passed from SelectDateActionsActivity
        selectedDate = getIntent().getStringExtra("selectedDate");

        if (selectedDate != null) {
            // You can use the selectedDate as needed, for example, displaying it
            showToast("Selected Date: " + selectedDate);
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.activities_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize activity list
        activityList = new ArrayList<>();

        // Set up the adapter
        adapter = new ActivityAdapterWithButtons(activityList, this);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference for the selected date
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the user is logged in
        if (currentUser != null) {
            String userId = currentUser.getUid();  // Get the logged-in user's UID
            dailyActivitiesRef = database.getReference("users")
                    .child(userId)  // Use the logged-in user's UID
                    .child("DailyActivities")
                    .child(selectedDate);

            // Fetch activities from Firebase
            fetchActivitiesFromFirebase();
        } else {
            // Handle the case when no user is logged in
            showToast("No user is logged in.");
        }
    }

    // Show a toast message
    private void showToast(String message) {
        Toast.makeText(ViewListActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void fetchActivitiesFromFirebase() {
        dailyActivitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    activityList.clear();  // Clear existing data before adding new ones

                    // Loop through categories
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        // Loop through the subcategories (e.g., 'drivePersonalVehicle', 'beef')
                        for (DataSnapshot subCategorySnapshot : categorySnapshot.getChildren()) {
                            String subCategoryName = subCategorySnapshot.getKey();

                            // Check if this subcategory has a 'CO2e' child (like 'beef' -> 'CO2e')
                            if (subCategorySnapshot.hasChild("CO2e")) {
                                // If there's a direct 'CO2e' child, get the value and add it to the list
                                Double co2eValue = subCategorySnapshot.child("CO2e").getValue(Double.class);
                                if (subCategoryName != null && co2eValue != null) {
                                    activityList.add(new ActivityItem(subCategoryName, co2eValue.toString()));
                                }
                            } else {
                                // If no direct 'CO2e', look for sub-subcategories (e.g., 'gasoline', 'diesel')
                                for (DataSnapshot subSubCategorySnapshot : subCategorySnapshot.getChildren()) {
                                    String subSubCategoryName = subSubCategorySnapshot.getKey();
                                    // Get the CO2e value under the subsubcategory
                                    Double co2eValue = subSubCategorySnapshot.child("CO2e").getValue(Double.class);
                                    if (subSubCategoryName != null && co2eValue != null) {
                                        activityList.add(new ActivityItem(subSubCategoryName, co2eValue.toString()));
                                    }
                                }
                            }
                        }
                    }

                    // Notify the adapter that data has changed
                    adapter.notifyDataSetChanged();
                } else {
                    // If no activities exist for this date, show a message
                    showToast("No activities for this date");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                showToast("Error fetching data: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onDeleteClick(ActivityItem activityItem) {
        // Remove the activity from the list
        activityList.remove(activityItem);
        adapter.notifyDataSetChanged();  // Refresh the RecyclerView to show updated data

        // Remove the activity from Firebase
        removeActivityFromFirebase(activityItem);

        // Show a confirmation toast
        showToast("Deleted: " + activityItem.getActivityName());
    }

    // Helper method to remove the activity from Firebase
    private void removeActivityFromFirebase(ActivityItem activityItem) {
        // The specific activity name is unique, so remove it by name.

        /// Iterate through the categories and subcategories in Firebase to find the correct path
        dailyActivitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        // Iterate through subcategories
                        for (DataSnapshot subCategorySnapshot : categorySnapshot.getChildren()) {
                            // Check if this subcategory name matches
                            if (subCategorySnapshot.getKey().equals(activityItem.getActivityName())) {
                                // Found the activity, remove it
                                subCategorySnapshot.getRef().removeValue();  // Removes this activity from Firebase
                                return;  // Exit after deleting
                            } else {
                                //Iterate through subsubcategories
                                for (DataSnapshot subSubCategorySnapshot : subCategorySnapshot.getChildren()) {
                                    //Check if this subsubcategory name matches
                                    if (subSubCategorySnapshot.getKey().equals(activityItem.getActivityName())) {
                                        //Found the activity, remove it
                                        subSubCategorySnapshot.getRef().removeValue();  // Removes this activity from Firebase
                                        return;  // Exit after deleting
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                showToast("Error removing data: " + databaseError.getMessage());
            }
        });
    }

}
