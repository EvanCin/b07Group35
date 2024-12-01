package com.example.planetze35;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EcoTrackerDailyActivityHub extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable EdgeToEdge immersive experience
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_tracker_daily_hub);
        // Get the selected date passed from the previous activity
        String selectedDate = getIntent().getStringExtra("selectedDate");

        // Apply window insets for immersive mode
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button transportButton = findViewById(R.id.button_transport);
        Button foodButton = findViewById(R.id.button_Food);
        Button consumptionButton = findViewById(R.id.button_Consumption);
        Button energyButton = findViewById(R.id.button_Energy);
        // Set up click listeners for each button to navigate to respective activities
        transportButton.setOnClickListener(v -> {
            Intent intent = new Intent(EcoTrackerDailyActivityHub.this, TransportationActivity.class);
            intent.putExtra("selectedDate", selectedDate);  // Pass the date to the next activity
            startActivity(intent);
        });

        foodButton.setOnClickListener(v -> {
            Intent intent = new Intent(EcoTrackerDailyActivityHub.this, FoodActivity.class);
            intent.putExtra("selectedDate", selectedDate);  // Pass the date to the next activity
            startActivity(intent);
        });
        energyButton.setOnClickListener(v -> {
            Intent intent = new Intent(EcoTrackerDailyActivityHub.this, EnergyBillsActivity.class);
            intent.putExtra("selectedDate", selectedDate);  // Pass the date to the next activity
            startActivity(intent);
        });
        consumptionButton.setOnClickListener(v -> {
            Intent intent = new Intent(EcoTrackerDailyActivityHub.this, ConsumptionEcoTrackerActivity.class);
            intent.putExtra("selectedDate", selectedDate);  // Pass the date to the next activity
            startActivity(intent);
        });

    }
}