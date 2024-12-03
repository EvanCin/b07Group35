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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NationalComparisonFragment extends Fragment {
    private LinearLayout gaugeAvgComparisonNationalAvgContainer;
    private TextView gaugeAvgComparisonNationalAvgOverview;
    private TextView gaugeAvgComparisonNationalUserEmissions;
    private TextView gaugeAvgComparisonNationalAvgHeader;
    private TextView gaugeAvgComparisonNationalAvgEmissions;
    private TextView gaugeAvgComparisonNationalAvgErrorMsg;
    private ProgressBar gaugeAvgComparisonNationalAvgProgressBar;

    private int loadProgress = 0;
    private final int PROGRESS_MAX = 3;

    private FirebaseUser currentUser;
    private String uid;
    private DatabaseReference dbUserRef;
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

        gaugeAvgComparisonNationalAvgContainer = view.findViewById(R.id.gaugeAvgComparisonNationalAvgContainer);
        gaugeAvgComparisonNationalAvgOverview = view.findViewById(R.id.gaugeAvgComparisonNationalAvgOverview);
        gaugeAvgComparisonNationalUserEmissions = view.findViewById(R.id.gaugeAvgComparisonNationalUserEmissions);
        gaugeAvgComparisonNationalAvgHeader = view.findViewById(R.id.gaugeAvgComparisonNationalAvgHeader);
        gaugeAvgComparisonNationalAvgEmissions = view.findViewById(R.id.gaugeAvgComparisonNationalAvgEmissions);
        gaugeAvgComparisonNationalAvgErrorMsg = view.findViewById(R.id.gaugeAvgComparisonNationalAvgErrorMsg);
        gaugeAvgComparisonNationalAvgProgressBar = view.findViewById(R.id.gaugeAvgComparisonNationalAvgProgressBar);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = (currentUser != null) ? currentUser.getUid() : "defaultUserId";
        dbUserRef = FirebaseDatabase.getInstance().getReference().child("users/" + uid);
        Log.d("NationalComparisonFragment", "Current user ID: " + uid);

        updateNationalAvgEmissionDisplay(context);
        updateUserAvgEmissionDisplay();
    }

    private void updateUserAvgEmissionDisplay() {
        DailyActivityEmissionsModel model = DailyActivityEmissionsModel.getInstance();
        LocalDate today = LocalDate.of(2025, 11, 20);
        model.getEmissionsData(today, "year", new DailyActivityEmissionsModel.DatabaseFetchCallback() {
            @Override
            public void onSuccess(HashMap<String, Double> dateToEmissionMapData) {
                int daysWithData = 0;
                double totalEmissions = 0.0;
                for (Map.Entry<String, Double> entry : dateToEmissionMapData.entrySet()) {
                    daysWithData++;
                    totalEmissions += entry.getValue();
                }

                double approxAnnualEmissions = totalEmissions * 0.365 / daysWithData;
                gaugeAvgComparisonNationalUserEmissions.setText(
                        String.format(Locale.getDefault(), "%.2f", approxAnnualEmissions));
                loadProgressIncrement();
            }

            @Override
            public void onFailure(@NonNull DatabaseError error) {
                Log.e("NationalComparisonFragment",
                        "Could not fetch Eco Tracker data from Firebase.",
                        error.toException());
            }
        });
    }

    private void updateNationalAvgEmissionDisplay(Context context) {
        country = "???";
        dbUserRef.child("country").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object o = snapshot.getValue();
                if (o instanceof String) {
                    country = (String) o;
                    Log.d("NationalComparisonFragment",
                            "Current user's country: " + country);
                    gaugeAvgComparisonNationalAvgHeader.setText("The average in " + country + " is");
                    loadProgressIncrement();

                    try {
                        GlobalAveragesCSVModel model = GlobalAveragesCSVModel.getInstance(context);
                        String countryEmissionAvgTxt = !country.equals("???")
                                ? String.format(Locale.getDefault(), "%.2f", model.getAvgEmission(country)) : country;
                        gaugeAvgComparisonNationalAvgEmissions.setText(countryEmissionAvgTxt);
                        loadProgressIncrement();
                    } catch (IOException e) {
                        Toast.makeText(context,
                                "Could not access local file containing global average emissions.",
                                Toast.LENGTH_SHORT).show();
                        loadFailed();
                    }
                } else {
                    Toast.makeText(context,
                            "Could not get the user's country from the database.",
                            Toast.LENGTH_SHORT).show();
                    Log.e("NationalComparisonFragment",
                            "Failed to get country of user (in onDataChange)");
                    loadFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,
                        "Could not get the user's country from the database.",
                        Toast.LENGTH_SHORT).show();
                Log.e("NationalComparisonFragment",
                        "Failed to get country of user (in onCancelled)", error.toException());
                loadFailed();
            }
        });
    }

    private void updateOverview() {
        double userEmissions = Double.parseDouble(gaugeAvgComparisonNationalUserEmissions.getText().toString());
        double avgEmissions = Double.parseDouble(gaugeAvgComparisonNationalAvgEmissions.getText().toString());
        double emissionRatio = userEmissions / avgEmissions;
        if (Math.abs(emissionRatio - 1) <= 0.08) {
            gaugeAvgComparisonNationalAvgOverview.setText("Your emissions are within the national average.");
        } else if ((emissionRatio - 1) > 0.08) {
            gaugeAvgComparisonNationalAvgOverview.setText("Your emissions are above the national average.");
        } else {
            gaugeAvgComparisonNationalAvgOverview.setText("Your emissions are below the national average.");
        }
    }

    private void loadProgressIncrement() {
        loadProgress++;
        if (loadProgress == PROGRESS_MAX) {
            updateOverview();
            gaugeAvgComparisonNationalAvgProgressBar.setVisibility(View.GONE);
            gaugeAvgComparisonNationalAvgContainer.setVisibility(View.VISIBLE);
        }
    }

    private void loadFailed() {
        loadProgress = Integer.MIN_VALUE;
        gaugeAvgComparisonNationalAvgProgressBar.setVisibility(View.GONE);
        gaugeAvgComparisonNationalAvgErrorMsg.setVisibility(ViewGroup.VISIBLE);
    }
}