package com.example.planetze35;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnergyBillsActivity extends AppCompatActivity {
    private Button energyBillsButton, addEnergyBillButton, doneButton;
    private String userId;
    private LinearLayout questionsLayout;
    private Spinner energyBillSpinner;
    private DatabaseReference databaseRef;
    private String selectedDate;
    private EditText billAmountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_energy_bills);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.energy), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //initialize firebase
        databaseRef = FirebaseDatabase.getInstance().getReference();
        selectedDate = getIntent().getStringExtra("selectedDate");
        userId = getUserId();
        if (userId == null) {
            // Handle the case where no user is logged in
            showToast("You must be logged in to add meal data.");
            return;
        }
        initializeViews();
        setupEnergyBillsButton();
        setupAddEnergyBillButton();
        setupEnergyBillSpinner();
        setupDoneButton();
    }
    private void initializeViews() {
        energyBillsButton = findViewById(R.id.energyBills_button);
        questionsLayout = findViewById(R.id.questionsMeal);
        addEnergyBillButton = findViewById(R.id.add_button_EnergyBills);
        doneButton = findViewById(R.id.done_button_energy);
        energyBillSpinner = findViewById(R.id.spinnerEnergyBill);
        billAmountEditText = findViewById(R.id.bill_energy);
        questionsLayout.setVisibility(View.GONE);
        addEnergyBillButton.setVisibility(View.GONE);
    }
    private void setupEnergyBillsButton() {
        energyBillsButton.setOnClickListener(v -> {
            if (questionsLayout.getVisibility() == View.VISIBLE) {
                questionsLayout.setVisibility(View.GONE);
                addEnergyBillButton.setVisibility(View.GONE);
            } else {
                questionsLayout.setVisibility(View.VISIBLE);
                addEnergyBillButton.setVisibility(View.VISIBLE);
            }
        });
    }
    private void setupAddEnergyBillButton() {
        addEnergyBillButton.setOnClickListener(v -> {
            String selectedEnergyType = energyBillSpinner.getSelectedItem().toString();
            String billAmount = billAmountEditText.getText().toString();

            if (isInputValid(selectedEnergyType, billAmount)) {
                addEnergyBill(selectedEnergyType, Double.parseDouble(billAmount));
            } else {
                showToast("Please select a bill type and enter the amount");
            }
        });
    }
    private boolean isInputValid(String selectedEnergyType, String billAmount) {
        return !selectedEnergyType.isEmpty() && !selectedEnergyType.equals("Select an energy type")
                && !billAmount.isEmpty();
    }
    private void addEnergyBill(String energyType, Double billAmount) {
        EnergyBills energybills = new EnergyBills(billAmount, energyType);
        Map<String, Object> energyData = energybills.toMap();
        Log.d("EnergyBillsActivity", "Adding energy bill data for user: " + userId + " on " + selectedDate);
        checkIfEnergyBillExistsForMonth(userId, selectedDate, energyType, energyData);
        resetFields();
    }
    private String getYearMonth(String selectedDate) {
        // Assuming selectedDate is in "yyyy-MM-dd" format (e.g., "2024-08-21")
        String[] dateParts = selectedDate.split("-"); // Split into [year, month, day]
        return dateParts[0] + "-" + dateParts[1];  // Return "2024-08"
    }
    private void resetFields() {
        billAmountEditText.setText("");
        energyBillSpinner.setSelection(0);
    }
    private void setupEnergyBillSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.energy_bill_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        energyBillSpinner.setAdapter(adapter);
    }
    private void showToast(String message) {
        Toast.makeText(EnergyBillsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private void setupDoneButton() {
        doneButton.setOnClickListener(v -> {
            // Redirect to another activity or perform a summary action
            Intent intent = new Intent(EnergyBillsActivity.this, EcoTrackerDailyActivityHub.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // End current activity
        });
    }
    private void checkIfEnergyBillExistsForMonth(String userId, String selectedDate, String energyType, Map<String, Object> energyData) {
        // Extract the year and month (yyyy-MM) from the selected date (yyyy-MM-dd)
        String selectedYearMonth = getYearMonth(selectedDate); // "yyyy-MM"

        // Reference to all daily activities for the user
        DatabaseReference userRef = databaseRef.child("users").child(userId)
                .child("DailyActivities");

        // Get all the days under DailyActivities (this will retrieve keys in yyyy-MM-dd format)
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean billExistsForSameMonth = false;
                boolean billExistsForSameDay = false;

                // Iterate through each day in DailyActivities (each child key is a date in yyyy-MM-dd format)
                for (DataSnapshot dateSnapshot : task.getResult().getChildren()) {
                    String dateKey = dateSnapshot.getKey();  // This is the date key in yyyy-MM-dd format
                    assert dateKey != null;
                    String yearMonth = getYearMonth(dateKey);  // Extract yyyy-MM part of the date

                    // Check if this date's year and month match the selected year and month
                    if (yearMonth.equals(selectedYearMonth)) {
                        // Bill exists in the same month, now check for the same energy type
                        DataSnapshot energyBillsSnapshot = dateSnapshot.child("energyBills");
                        if (energyBillsSnapshot.hasChild(energyType)) {
                            if (dateKey.equals(selectedDate)) {
                                billExistsForSameDay = true;  // Bill exists for the same day, allow overwriting
                            }
                            billExistsForSameMonth = true;  // Found a bill for the same month
                        }
                    }
                }
                // Allow overwriting if the bill exists for the same day
                if (billExistsForSameDay) {
                    Log.d("EnergyBillsActivity", "Bill exists for the same day, overwriting...");
                    addOrUpdateEnergyHelper(userId, selectedDate, energyData, energyType);
                } else if (billExistsForSameMonth) {
                    // Prevent adding a new bill if the energy type already exists for a different day in the same month
                    showToast("A bill for " + energyType + " already exists for this month. You can only overwrite the bill on the same day.");
                } else {
                    // No bill found for this energy type in the month, allow adding
                    Log.d("EnergyBillsActivity", "No bill found for this energy type in this month, adding new one...");
                    addOrUpdateEnergyHelper(userId, selectedDate, energyData, energyType);
                }
            } else {
                // Handle potential error if the task fails
                Log.e("EnergyBillsActivity", "Error checking bill: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }
    private void addOrUpdateEnergyHelper(String userId, String date, Map<String, Object> energyData, String energyType) {
        databaseRef.child("users").child(userId).child("DailyActivities").child(date)
                .child("energyBills").child(energyType).setValue(energyData)
                .addOnCompleteListener(addTask -> {
                    if (addTask.isSuccessful()) {
                        showToast("Added " + energyType + " data for " + date);
                        Log.d("EnergyBillsActivity", "Successfully added " + energyType + " for " + date);
                        resetFields();
                    } else {
                        Log.e("EnergyBillsActivity", "Error adding energy data: " + Objects.requireNonNull(addTask.getException()).getMessage());
                        showToast("Error: " + Objects.requireNonNull(addTask.getException()).getMessage());
                    }
                });
    }
    // Helper function to get user ID (replace with Firebase Auth method)
    private String getUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If a user is logged in, return their UID
            return currentUser.getUid();
        } else {
            // Handle case when no user is logged in
            Log.e("EnergyActivity", "No user is logged in");
            return null;  // or throw an exception, or handle as needed
        }
    }
}