package com.example.planetze35;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class NationalComparisonFragment extends Fragment {
    private TextView gaugeAvgComparisonNationalUserEmissions;
    private TextView gaugeAvgComparisonNationalAvgHeader;
    private TextView gaugeAvgComparisonNationalAvgEmissions;
    private FirebaseUser currentUser;
    private String uid;
    private DatabaseReference dbUserRef;
    private DatabaseReference dbUserCountryRef;
    private ValueEventListener countryGetterListener;
    private String country;

    public NationalComparisonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_national_comparison, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Context context = view.getContext();

        gaugeAvgComparisonNationalUserEmissions = view.findViewById(R.id.gaugeAvgComparisonNationalUserEmissions);
        gaugeAvgComparisonNationalAvgHeader = view.findViewById(R.id.gaugeAvgComparisonNationalAvgHeader);
        gaugeAvgComparisonNationalAvgEmissions = view.findViewById(R.id.gaugeAvgComparisonNationalAvgEmissions);

        // Get database node containing current user's data
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = (currentUser != null) ? currentUser.getUid() : "defaultUserId";
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("users/" + uid);
        Log.d("NationalComparisonFragment", "Current user ID: " + uid);

        updateNationalAvgEmissionDisplay(context);

        // TODO: Get the user's emission average once that is implemented
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbUserCountryRef.removeEventListener(countryGetterListener);
    }

    public void updateNationalAvgEmissionDisplay(Context context) {
        country = "???";
        dbUserCountryRef = dbUserRef.child("country");
        countryGetterListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object o = snapshot.getValue();
                if (o instanceof String) {
                    country = (String) o;
                    Log.d("NationalComparisonFragment",
                            "Current user's country: " + country);
                    gaugeAvgComparisonNationalAvgHeader.setText("The average in " + country + " is");

                    try {
                        AvgEmissionModel model = AvgEmissionModel.getInstance(context);
                        String countryEmissionAvgTxt = !country.equals("???")
                                ? String.format("%.2f", model.getAvgEmission(country)) : country;
                        gaugeAvgComparisonNationalAvgEmissions.setText(countryEmissionAvgTxt);
                    } catch (IOException e) {
                        Toast.makeText(context,
                                "Could not access local file containing global average emissions.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context,
                            "Could not get the user's country from the database.",
                            Toast.LENGTH_SHORT).show();
                    Log.e("NationalComparisonFragment",
                            "Failed to get country of user (in onDataChange)");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,
                        "Could not get the user's country from the database.",
                        Toast.LENGTH_SHORT).show();
                Log.e("NationalComparisonFragment",
                        "Failed to get country of user (in onCancelled)", error.toException());
            }
        };
        dbUserCountryRef.addListenerForSingleValueEvent(countryGetterListener);
    }
}