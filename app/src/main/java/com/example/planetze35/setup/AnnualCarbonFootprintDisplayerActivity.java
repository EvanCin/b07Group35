package com.example.planetze35.setup;

import android.os.Bundle;
import android.widget.TextView;

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

public class AnnualCarbonFootprintDisplayerActivity extends AppCompatActivity {

    private TextView tvTotalAnnualEmissionsNumber;
    private TextView tbRowTransportation, tbRowFood, tbRowHousing, tbRowConsumption;
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
        tbRowTransportation = findViewById(R.id.tbRowTransportation);
        tbRowFood = findViewById(R.id.tbRowFood);
        tbRowHousing = findViewById(R.id.tbRowHousing);
        tbRowConsumption = findViewById(R.id.tbRowConsumption);
        dbRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        showEmissions();
    }

    private void showEmissions() {
        dbRef.child("users").child(user.getUid()).child("totalAnnualEmissionsByCategory").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Object value = task.getResult().getValue();
                if (!(value instanceof Map)) {
                    return;
                }
                Map<String, Object> emissionsByCategory = (Map<String, Object>) value;
                showTotalAnnualEmissions(emissionsByCategory.get("total").toString());
                emissionsByCategory.remove("total");
                showEmissionsByCategory(emissionsByCategory);
            }
        });
    }

    private void showTotalAnnualEmissions(String totalAnnualEmissions) {
        tvTotalAnnualEmissionsNumber.setText(totalAnnualEmissions);
    }

    private void showEmissionsByCategory(Map<String, Object> map) {
        tbRowTransportation.setText(map.get("transportation").toString());
        tbRowFood.setText(map.get("food").toString());
        tbRowHousing.setText(map.get("housing").toString());
        tbRowConsumption.setText(map.get("consumption").toString());
    }
}