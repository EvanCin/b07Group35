package com.example.planetze35;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EcoTrackerHomepageActivity extends AppCompatActivity {

    private List<ActivityItem> activityList;
    private TextView totalCo2TextView;
    private RecyclerView activityRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_tracker_homepage);  // Use new layout for EcoTrackerHomepageActivity

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.eco_tracker_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize TextView for total CO2e emissions
        totalCo2TextView = findViewById(R.id.total_co2);

        // Initialize RecyclerView
        activityRecyclerView = findViewById(R.id.activity_breakdown_list);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a list of activities with hardcoded data
        activityList = new ArrayList<>();
        activityList.add(new ActivityItem("Car ride", "200g CO2e"));
        activityList.add(new ActivityItem("Electricity usage", "150g CO2e"));
        activityList.add(new ActivityItem("Gas heating", "250g CO2e"));
        activityList.add(new ActivityItem("Food consumption", "100g CO2e"));

        // Set up the adapter with the activity list
        ActivityAdapterNoButtons adapter = new ActivityAdapterNoButtons(activityList);
        activityRecyclerView.setAdapter(adapter);

        // Calculate total CO2e emissions for the day
        int totalCo2e = 0;
        for (ActivityItem activity : activityList) {
            String co2eString = activity.getCo2Value();
            int co2eValue = Integer.parseInt(co2eString.replaceAll("[^0-9]", ""));
            totalCo2e += co2eValue;
        }

        // Update the total CO2e TextView with the calculated total
        totalCo2TextView.setText("Total CO2e Emissions: " + totalCo2e + "g");

        // Initialize the "Activity Management" button
        Button btnActivityManagement = findViewById(R.id.btn_activity_management);
        btnActivityManagement.setOnClickListener(v -> {
            // Navigate to DatePickerActivity
            Intent intent = new Intent(EcoTrackerHomepageActivity.this, DatePickerActivity.class);
            startActivity(intent);
        });
    }
}
