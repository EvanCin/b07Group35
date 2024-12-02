package com.example.planetze35.setup;

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

import com.example.planetze35.GlobalAveragesCSVModel;
import com.example.planetze35.MainActivity;
import com.example.planetze35.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Map;

public class AnnualCarbonFootprintDisplayerActivity extends AppCompatActivity {

    private TextView tvTotalAnnualEmissionsNumber, tvCompareWithRegionalAverage;
    private TextView tbRowTransportation, tbRowFood, tbRowHousing, tbRowConsumption;
    private Button btnRecalculate, btnGoToMainMenu;
    private DatabaseReference dbRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_annual_carbon_footprint_displayer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvTotalAnnualEmissionsNumber = findViewById(R.id.tvTotalAnnualEmissionsNumber);
        tvCompareWithRegionalAverage = findViewById(R.id.tvCompareWithRegionalAverage);
        tbRowTransportation = findViewById(R.id.tbRowTransportation);
        tbRowFood = findViewById(R.id.tbRowFood);
        tbRowHousing = findViewById(R.id.tbRowHousing);
        tbRowConsumption = findViewById(R.id.tbRowConsumption);
        btnRecalculate = findViewById(R.id.btnRecalculate);
        btnGoToMainMenu = findViewById(R.id.btnGoToMainMenu);
        dbRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        displayAnnualCarbonFootprint();

        btnRecalculate.setOnClickListener(v -> {
            Intent intent = new Intent(this, AnnualCarbonFootprintActivity.class);
            startActivity(intent);
            finish();
        });

        btnGoToMainMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayAnnualCarbonFootprint() {
        dbRef.child("users").child(user.getUid()).child("totalAnnualEmissionsByCategory").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Object value = task.getResult().getValue();
                if (!(value instanceof Map)) {
                    return;
                }

                Map<String, String> emissionsByCategory = (Map<String, String>) value;
                showTotalAnnualEmissions(emissionsByCategory.get("total"));
                showEmissionsByCategory(emissionsByCategory);
                compareWithRegionalAverage(emissionsByCategory.get("total"));
            }
        });
    }

    private void showTotalAnnualEmissions(String totalAnnualEmissions) {
        tvTotalAnnualEmissionsNumber.setText(totalAnnualEmissions);
    }

    private void showEmissionsByCategory(Map<String, String> map) {
        tbRowTransportation.setText(map.get("transportation"));
        tbRowFood.setText(map.get("food"));
        tbRowHousing.setText(map.get("housing"));
        tbRowConsumption.setText(map.get("consumption"));
    }

    private void compareWithRegionalAverage(String totalAnnualEmissions) {
        try {
            GlobalAveragesCSVModel globalAveragesCSVModel = GlobalAveragesCSVModel.getInstance(this);
            dbRef.child("users").child(user.getUid()).child("country").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String country = (String) task.getResult().getValue();
                    String regionalEmissionsAverage = String.format("%.2f", globalAveragesCSVModel.getAvgEmission(country));
                    double diff = calculatePercentageDifference(Double.parseDouble(totalAnnualEmissions),Double.parseDouble(regionalEmissionsAverage));
                    if (diff > 0) {
                        tvCompareWithRegionalAverage.setTextColor(getResources().getColor(R.color.red));
                        tvCompareWithRegionalAverage.setText("Your total annual emissions are " + String.format("%.2f", Math.abs(diff)) + "% higher than the regional average.");
                    } else if (diff < 0) {
                        tvCompareWithRegionalAverage.setTextColor(getResources().getColor(R.color.teal_200));
                        tvCompareWithRegionalAverage.setText("Your total annual emissions are " + String.format("%.2f", Math.abs(diff)) + "% lower than the regional average.");
                    } else {
                        tvCompareWithRegionalAverage.setTextColor(getResources().getColor(R.color.teal_200));
                        tvCompareWithRegionalAverage.setText("Your total annual emissions are equal to the regional average.");
                    }
                }
            });
        } catch (IOException e) {
            Toast.makeText(this,
                    "Could not access local file containing global average emissions.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private double calculatePercentageDifference(double totalAnnualEmissions, double regionalEmissionsAverage) {
        return ((totalAnnualEmissions - regionalEmissionsAverage) / regionalEmissionsAverage) * 100;
    }
}