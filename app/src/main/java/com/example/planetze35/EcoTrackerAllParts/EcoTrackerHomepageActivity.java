package com.example.planetze35.EcoTrackerAllParts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planetze35.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EcoTrackerHomepageActivity extends AppCompatActivity {

    private List<ActivityItem> activityList;
    private TextView totalCo2TextView;
    private RecyclerView activityRecyclerView;
    private FirebaseDatabase database;
    private DatabaseReference dailyActivitiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_tracker_homepage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.eco_tracker_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView and set the layout manager
        activityRecyclerView = findViewById(R.id.activity_breakdown_list);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set LayoutManager

        // Initialize the TextView for total CO2e emissions
        totalCo2TextView = findViewById(R.id.total_co2);

        // Initialize Firebase Database reference for the selected date
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the user is logged in
        if (currentUser == null) {
            // User is not logged in, show a Toast message
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_LONG).show();
        } else {
            // User is logged in, proceed with fetching data
            String userId = currentUser.getUid(); // Get current user ID

            // Get the current date formatted using DateUtils
            String currentDate = DateUtils.getCurrentDateFormatted();

            // Reference to the current day's activities
            dailyActivitiesRef = database.getReference("users").child(userId).child("DailyActivities").child(currentDate);

            // Create the adapter and set it to the RecyclerView
            activityList = new ArrayList<>();
            ActivityAdapterNoButtons adapter = new ActivityAdapterNoButtons(activityList);
            activityRecyclerView.setAdapter(adapter);

            // Fetch daily activities and emissions
            fetchDailyActivities();

            // Initialize the "Activity Management" button
            Button btnActivityManagement = findViewById(R.id.btn_activity_management);
            btnActivityManagement.setOnClickListener(v -> {
                // Navigate to DatePickerActivity
                Intent intent = new Intent(EcoTrackerHomepageActivity.this, DatePickerActivity.class);
                startActivity(intent);
            });

            // Initialize the "Habit Suggestions" button
            Button btnHabitSuggestions = findViewById(R.id.btn_habit_suggestions);
            btnHabitSuggestions.setOnClickListener(v -> {
                // Navigate to RecommendationsActivity
                Intent intent = new Intent(EcoTrackerHomepageActivity.this, RecommendationsActivity.class);
                startActivity(intent);
            });
        }
    }

    private void fetchDailyActivities() {
        dailyActivitiesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    activityList.clear();  // Clear existing data

                    // Loop through categories and add them to the activity list
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

                    // After fetching activities, now fetch and display the total CO2e emissions
                    if (dataSnapshot.hasChild("total_daily_emissions")) {
                        // Get total CO2e from Firebase
                        Double totalCo2eFromFirebase = dataSnapshot.child("total_daily_emissions").getValue(Double.class);
                        if (totalCo2eFromFirebase != null) {
                            totalCo2TextView.setText("Total CO2e: " + totalCo2eFromFirebase + " kg");
                        } else {
                            // If the total_daily_emissions is null, set to 0
                            totalCo2TextView.setText("Total CO2e: 0 kg");
                        }
                    } else {
                        // If total_daily_emissions doesn't exist, set the value to 0
                        totalCo2TextView.setText("Total CO2e: 0 kg");
                    }

                    // Notify the adapter that the data has changed and it should update the view
                    activityRecyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    // If no data exists for this date, display 0kg and don't update Firebase
                    totalCo2TextView.setText("Total CO2e: 0 kg");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                System.out.println("Error fetching data: " + databaseError.getMessage());
            }
        });
    }
}
