package com.example.planetze35.EcoTrackerAllParts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Objects;

public class ConsumptionEcoTrackerActivity extends AppCompatActivity {

    private Button buttonNewClothes, addNewClothesButton;
    private DatabaseReference databaseRef;
    private String selectedDate;
    private String userId;
    private LinearLayout questionsNewClothes;
    private EditText numClothes;

    // UI components for Electronics
    private Button buttonElectronics, addElectronicsButton;
    private LinearLayout questionsElectronics;
    private EditText numDevices;
    private Spinner spinnerElectronics;  // Spinner for electronics selection

    // UI components for Done Button
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consumption_eco_tracker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.consumption), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseRef = FirebaseDatabase.getInstance().getReference();
        // Retrieve the selected date passed from EcoTrackerDailyActivityHub
        selectedDate = getIntent().getStringExtra("selectedDate");
        userId = getUserId();
        if (userId == null) {
            // Handle the case where no user is logged in
            showToast("You must be logged in to add consumption data.");
            return;
        }
        initUIComponents();
        // Set up ScrollView for scrolling when sections appear
        ScrollView scrollView = findViewById(R.id.consumption);
        // Set onClick listeners
        setOnClickListeners(scrollView);
    }
    private void initUIComponents() {
        // New Clothes components
        buttonNewClothes = findViewById(R.id.button_new_clothes);
        questionsNewClothes = findViewById(R.id.questionsNewClothes);
        numClothes = findViewById(R.id.num_clothes);
        addNewClothesButton = findViewById(R.id.add_button_new_clothes);

        // Electronics components
        buttonElectronics = findViewById(R.id.button_electronics);
        questionsElectronics = findViewById(R.id.questionsElectronics);
        numDevices = findViewById(R.id.num_devices);
        addElectronicsButton = findViewById(R.id.add_button_Electronics);
        spinnerElectronics = findViewById(R.id.spinnerElectronics);  // Initialize the Electronics spinner

        // Done button
        doneButton = findViewById(R.id.done_button);

        // Set up Spinners with adapters for Electronics and Other Purchases
        setupSpinners();
    }
    private void setupSpinners() {
        // Spinner for Electronics - types of devices
        ArrayAdapter<CharSequence> electronicsAdapter = ArrayAdapter.createFromResource(this,
                R.array.electronics_types, android.R.layout.simple_spinner_item);
        electronicsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerElectronics.setAdapter(electronicsAdapter);
    }
    private void setOnClickListeners(ScrollView scrollView) {
        // New Clothes button action
        buttonNewClothes.setOnClickListener(v -> toggleVisibility(questionsNewClothes, scrollView));

        // Electronics button action
        buttonElectronics.setOnClickListener(v -> toggleVisibility(questionsElectronics, scrollView));

        // Done button action
        doneButton.setOnClickListener(v -> scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN)));

        doneButton.setOnClickListener(v -> {
            // Redirect to the main page
            Intent intent = new Intent(ConsumptionEcoTrackerActivity.this, EcoTrackerDailyActivityHub.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        // Add New Clothes button action
        addNewClothesButton.setOnClickListener(v -> handleAddNewClothes());
        // Add Electronics button action
        addElectronicsButton.setOnClickListener(v -> handleAddElectronics());
    }
    // Helper method to toggle visibility of each section
    private void toggleVisibility(LinearLayout selectedLayout, ScrollView scrollView) {
        questionsNewClothes.setVisibility(View.GONE);
        questionsElectronics.setVisibility(View.GONE);
        selectedLayout.setVisibility(View.VISIBLE);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
    // Method to handle adding New Clothes
    private void handleAddNewClothes() {
        String clothesCountText = numClothes.getText().toString();
        if (!clothesCountText.isEmpty()) {
            int clothCount = Integer.parseInt(clothesCountText);
            NewClothes clothes = new NewClothes(clothCount);
            // Prepare data to be stored
            Map<String, Object> purchaseData = clothes.toMap();
            //store to firebase
            updateOrAddConsumptionData(userId, selectedDate, "newClothes", null, purchaseData);
            numClothes.setText("");
        } else {
            showToast("Please enter the number of clothing items");
        }
    }
    // Method to handle adding Electronics
    private void handleAddElectronics() {
        String devicesCountText = numDevices.getText().toString();
        String deviceType = spinnerElectronics.getSelectedItem().toString(); // Get selected item from Spinner

        if (!devicesCountText.isEmpty()) {
            int deviceCount = Integer.parseInt(devicesCountText);
            Electronics electronics = new Electronics(deviceCount, deviceType);
            // Prepare data to be stored
            Map<String, Object> purchaseData = electronics.toMap();
            updateOrAddConsumptionData(userId, selectedDate, "electronics", deviceType, purchaseData);
            numDevices.setText("");
        } else {
            showToast("Please enter the number of electronics");
        }
    }
    private void updateOrAddConsumptionData(String userId, String date, String ConsumptionType, String subItemType, Map<String, Object> purchaseData) {
        if (subItemType != null) {
            // Store data under the specific transportation subtype (e.g., gasoline, bus)
            databaseRef.child("users").child(userId).child("DailyActivities").child(date)
                    .child("consumption").child(ConsumptionType).child(subItemType)
                    .setValue(purchaseData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showToast("Added " + ConsumptionType + " data for " + date);
                        } else {
                            showToast("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        } else {
            // For newClothes data (no subtype)
            databaseRef.child("users").child(userId).child("DailyActivities").child(date)
                    .child("consumption").child(ConsumptionType)
                    .setValue(purchaseData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showToast("Added " + ConsumptionType + " data for " + date);
                        } else {
                            showToast("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        }
    }
    // Helper function to get user ID (replace with Firebase Auth method)
    private String getUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If a user is logged in, return their UID
            return currentUser.getUid();
        } else {
            // Handle case when no user is logged in
            Log.e("ConsumptionActivity", "No user is logged in");
            return null;  // or throw an exception, or handle as needed
        }
    }
    // Helper method to show Toast messages
    private void showToast(String message) {
        Toast.makeText(ConsumptionEcoTrackerActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}