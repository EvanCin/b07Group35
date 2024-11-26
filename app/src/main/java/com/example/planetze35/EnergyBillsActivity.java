package com.example.planetze35;

import android.content.Intent;
import android.os.Bundle;
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

public class EnergyBillsActivity extends AppCompatActivity {
    private Button energyBillsButton, addEnergyBillButton, doneButton;
    private LinearLayout questionsLayout;
    private Spinner energyBillSpinner;
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
                addEnergyBill(selectedEnergyType, billAmount);
            }
        });
    }
    private boolean isInputValid(String selectedEnergyType, String billAmount) {
        if (selectedEnergyType.isEmpty() || selectedEnergyType.equals("Select an energy type")) {
            showToast("Please select an energy type");
            return false;
        } else if (billAmount.isEmpty()) {
            showToast("Please enter the bill amount");
            return false;
        }
        return true;
    }
    private void addEnergyBill(String selectedEnergyType, String billAmount) {
        showToast("Energy bill added successfully!");
        resetFields();
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

}