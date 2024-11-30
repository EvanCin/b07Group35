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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Objects;


public class FoodActivity extends AppCompatActivity {
    private DatabaseReference databaseRef;
    private String selectedDate;
    private String userId;
    private Button mealButton, addButton, doneButton;
    private LinearLayout questionsLayout;
    private Spinner mealSpinner;
    private EditText servingsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.food), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Retrieve the selected date passed from EcoTrackerDailyActivityHub
        selectedDate = getIntent().getStringExtra("selectedDate");
        //initialize firebase
        databaseRef = FirebaseDatabase.getInstance().getReference();
        userId = getUserId();
        if (userId == null) {
            // Handle the case where no user is logged in
            showToast("You must be logged in to add meal data.");
            return;
        }
        initializeViews();
        setupMealButton();
        setupAddButton();
        setupMealSpinner();
        setupDoneButton();
    }
    private void initializeViews() {
        mealButton = findViewById(R.id.meal_button);
        questionsLayout = findViewById(R.id.questionsMeal);
        addButton = findViewById(R.id.add_button);
        doneButton = findViewById(R.id.done_button);
        mealSpinner = findViewById(R.id.spinnerMeal);
        servingsEditText = findViewById(R.id.numServings);
        questionsLayout.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
    }
    private void setupMealButton() {
        mealButton.setOnClickListener(v -> {
            if (questionsLayout.getVisibility() == View.VISIBLE) {
                questionsLayout.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
            } else {
                questionsLayout.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
            }
        });
    }
    private void setupAddButton() {
        addButton.setOnClickListener(v -> {
            String selectedMealType = mealSpinner.getSelectedItem().toString();
            String servingsText = servingsEditText.getText().toString();

            if (isInputValid(selectedMealType, servingsText)) {
                addMeal(selectedMealType,Integer.parseInt(servingsText));
            } else {
                showToast("Please select a meal type and enter the number of servings");
            }
        });
    }

    private boolean isInputValid(String selectedMealType, String servings) {
        if (selectedMealType.isEmpty() || selectedMealType.equals("Select a meal")) {
            showToast("Please select a meal type");
            return false;
        } else if (servings.isEmpty()) {
            showToast("Please enter the number of servings");
            return false;
        }
        return true;
    }

    public void addMeal(String mealType, int servings) {
        Meal meal = new Meal(servings, mealType);
        Map<String, Object> mealData = meal.toMap();
        Log.d("FoodActivity", "Adding meal data for user: " + userId + " on " + selectedDate);
        addOrUpdateMealHelper(userId, selectedDate, mealData, mealType);
    }
    private void addOrUpdateMealHelper(String userId, String date, Map<String, Object> mealData, String mealType) {
        databaseRef.child("users").child(userId).child("DailyActivities").child(date)
                .child("food").child(mealType).setValue(mealData)
                .addOnCompleteListener(addTask -> {
                    if (addTask.isSuccessful()) {
                        showToast("Added " + mealType + " data for " + date);
                        Log.d("FoodActivity", "Successfully added " + mealType + " for " + date);
                        resetFields();
                    } else {
                        Log.e("FoodActivity", "Error adding meal data: " + Objects.requireNonNull(addTask.getException()).getMessage());
                        showToast("Error: " + Objects.requireNonNull(addTask.getException()).getMessage());
                    }
                });
    }
    private void resetFields() {
        servingsEditText.setText("");
        mealSpinner.setSelection(0);
    }
    private void setupMealSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealSpinner.setAdapter(adapter);
    }

    private void showToast(String message) {
        Toast.makeText(FoodActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private void setupDoneButton() {
        doneButton.setOnClickListener(v -> {
            // Navigate back to the MainActivity
            Intent intent = new Intent(FoodActivity.this, EcoTrackerDailyActivityHub.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close the current activity
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
            Log.e("FoodActivity", "No user is logged in");
            return null;  // or throw an exception, or handle as needed
        }
    }
}