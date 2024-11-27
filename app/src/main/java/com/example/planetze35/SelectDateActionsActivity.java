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

public class SelectDateActionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_date_actions);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.select_date_actions), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the selected date passed from DatePickerActivity
        Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("selectedDate");

        // Display the selected date
        TextView selectedDateText = findViewById(R.id.selected_date_text);
        selectedDateText.setText("Selected Date: " + selectedDate);

        // Set up the buttons
        Button buttonAddActivity = findViewById(R.id.button_add_activity);
        Button buttonViewActivities = findViewById(R.id.button_view_activities);

        // Set up the "Add Activity" button (implement functionality later)
        buttonAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Set up the "View Activities" button
        buttonViewActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent(SelectDateActionsActivity.this, ViewListActivity.class);

                // Pass the selected date to the next activity
                viewIntent.putExtra("selectedDate", selectedDate);
                startActivity(viewIntent);
            }
        });
    }
}
