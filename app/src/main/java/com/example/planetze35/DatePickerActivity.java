package com.example.planetze35;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DatePickerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_date_picker);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pick_date), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnSelectDate = findViewById(R.id.btn_select_date);  // The select date button
        final DatePicker datePicker = findViewById(R.id.datePicker);  // Date picker widget

        // Set onClickListener for the "Select Date" button
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected date from the DatePicker
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                // Format the date as a string
                String selectedDate = year + "-" + (month + 1) + "-" + day;

                // Show a Toast message with the selected date
                Toast.makeText(DatePickerActivity.this, "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();

                // Pass the selected date to SelectDateActionsActivity
                Intent intent = new Intent(DatePickerActivity.this, SelectDateActionsActivity.class);
                intent.putExtra("selectedDate", selectedDate);
                startActivity(intent);
            }
        });
    }
}
