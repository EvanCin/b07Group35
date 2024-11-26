package com.example.planetze35;

import android.content.Intent;
import android.os.Bundle;
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

public class ConsumptionActivity extends AppCompatActivity {

    private Button buttonNewClothes, addNewClothesButton;
    private LinearLayout questionsNewClothes;
    private EditText numClothes;

    // UI components for Electronics
    private Button buttonElectronics, addElectronicsButton;
    private LinearLayout questionsElectronics;
    private EditText numDevices;
    private Spinner spinnerElectronics;  // Spinner for electronics selection

    // UI components for Other Purchases
    private Button buttonOtherPurchase, addOtherPurchaseButton;
    private LinearLayout questionsOtherPurchase;
    private EditText numPurchase;
    private Spinner spinnerOtherPurchase;  // Spinner for other purchase selection

    // UI components for Done Button
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consumption);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.consumption), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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

        // Other Purchases components
        buttonOtherPurchase = findViewById(R.id.button_other_purchase);
        questionsOtherPurchase = findViewById(R.id.questions_other_purchase);
        numPurchase = findViewById(R.id.num_purchase);
        addOtherPurchaseButton = findViewById(R.id.add_button_other_purchase);
        spinnerOtherPurchase = findViewById(R.id.spinner_Other_Purchase);  // Initialize the Other Purchases spinner

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

        // Spinner for Other Purchases - categories of purchases
        ArrayAdapter<CharSequence> otherPurchaseAdapter = ArrayAdapter.createFromResource(this,
                R.array.purchase_types, android.R.layout.simple_spinner_item);
        otherPurchaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOtherPurchase.setAdapter(otherPurchaseAdapter);
    }
    private void setOnClickListeners(ScrollView scrollView) {
        // New Clothes button action
        buttonNewClothes.setOnClickListener(v -> toggleVisibility(questionsNewClothes, scrollView));

        // Electronics button action
        buttonElectronics.setOnClickListener(v -> toggleVisibility(questionsElectronics, scrollView));

        // Other Purchases button action
        buttonOtherPurchase.setOnClickListener(v -> toggleVisibility(questionsOtherPurchase, scrollView));

        // Done button action
        doneButton.setOnClickListener(v -> scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN)));

        doneButton.setOnClickListener(v -> {
            // Redirect to the main page
            Intent intent = new Intent(ConsumptionActivity.this, EcoTrackerDailyActivityHub.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Add New Clothes button action
        addNewClothesButton.setOnClickListener(v -> handleAddNewClothes());

        // Add Electronics button action
        addElectronicsButton.setOnClickListener(v -> handleAddElectronics());

        // Add Other Purchases button action
        addOtherPurchaseButton.setOnClickListener(v -> handleAddOtherPurchases());
    }
    // Helper method to toggle visibility of each section
    private void toggleVisibility(LinearLayout selectedLayout, ScrollView scrollView) {
        questionsNewClothes.setVisibility(View.GONE);
        questionsElectronics.setVisibility(View.GONE);
        questionsOtherPurchase.setVisibility(View.GONE);

        selectedLayout.setVisibility(View.VISIBLE);

        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
    // Method to handle adding New Clothes
    private void handleAddNewClothes() {
        String clothesCount = numClothes.getText().toString();
        if (!clothesCount.isEmpty()) {
            showToast("Added: " + clothesCount + " clothing items");
            numClothes.setText("");
        } else {
            showToast("Please enter the number of clothing items");
        }
    }

    // Method to handle adding Electronics
    private void handleAddElectronics() {
        String devicesCount = numDevices.getText().toString();
        String deviceType = spinnerElectronics.getSelectedItem().toString(); // Get selected item from Spinner

        if (!devicesCount.isEmpty()) {
            showToast("Added: " + devicesCount + " " + deviceType + " electronics");
            numDevices.setText("");
        } else {
            showToast("Please enter the number of electronics");
        }
    }
    // Method to handle adding Other Purchases
    private void handleAddOtherPurchases() {
        String purchaseCount = numPurchase.getText().toString();
        String purchaseType = spinnerOtherPurchase.getSelectedItem().toString(); // Get selected item from Spinner

        if (!purchaseCount.isEmpty()) {
            showToast("Added: " + purchaseCount + " " + purchaseType + " purchases");
            numPurchase.setText("");
        } else {
            showToast("Please enter the number of purchases");
        }
    }

    // Helper method to show Toast messages
    private void showToast(String message) {
        Toast.makeText(ConsumptionActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}