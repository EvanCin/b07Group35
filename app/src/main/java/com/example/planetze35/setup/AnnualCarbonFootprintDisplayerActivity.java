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

public class AnnualCarbonFootprintDisplayerActivity extends AppCompatActivity {

    private TextView tvTotalAnnualEmissionsNumber;
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
        dbRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        showTotalAnnualEmissions();
    }

    private void showTotalAnnualEmissions() {
        dbRef.child("users").child(user.getUid()).child("totalAnnualEmissionsByCategory/total").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tvTotalAnnualEmissionsNumber.setText(task.getResult().getValue(Double.class).toString());
            }
        });
    }
}