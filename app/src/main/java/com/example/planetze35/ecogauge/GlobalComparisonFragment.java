package com.example.planetze35.ecogauge;

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

import com.example.planetze35.R;
import com.google.firebase.database.DatabaseError;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GlobalComparisonFragment extends Fragment {

    private LinearLayout gaugeAvgComparisonGlobalAvgContainer;
    private TextView gaugeAvgComparisonGlobalAvgOverview;
    private TextView gaugeAvgComparisonGlobalUserEmissions;
    private TextView gaugeAvgComparisonGlobalAvgEmissions;
    private ProgressBar gaugeAvgComparisonGlobalAvgProgressBar;
    private TextView gaugeAvgComparisonGlobalAvgErrorMsg;

    private int loadProgress = 0;
    private final int PROGRESS_MAX = 2;

    public GlobalComparisonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_global_comparison, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Context context = view.getContext();

        gaugeAvgComparisonGlobalAvgContainer = view.findViewById(R.id.gaugeAvgComparisonGlobalAvgContainer);
        gaugeAvgComparisonGlobalAvgOverview = view.findViewById(R.id.gaugeAvgComparisonGlobalAvgOverview);
        gaugeAvgComparisonGlobalUserEmissions = view.findViewById(R.id.gaugeAvgComparisonGlobalUserEmissions);
        gaugeAvgComparisonGlobalAvgEmissions = view.findViewById(R.id.gaugeAvgComparisonGlobalAvgEmissions);
        gaugeAvgComparisonGlobalAvgProgressBar = view.findViewById(R.id.gaugeAvgComparisonGlobalAvgProgressBar);
        gaugeAvgComparisonGlobalAvgErrorMsg = view.findViewById(R.id.gaugeAvgComparisonGlobalAvgErrorMsg);

        updateGlobalAvgEmissionDisplay(context);
        updateUserAvgEmissionDisplay();
    }

    private void updateUserAvgEmissionDisplay() {
        DailyActivityEmissionsModel model = DailyActivityEmissionsModel.getInstance();
        LocalDate today = LocalDate.now();
        model.getEmissionsData(today, "year", new DailyActivityEmissionsModel.DatabaseFetchCallback() {
            @Override
            public void onSuccess(HashMap<String, Double> dateToEmissionMapData) {
                int daysWithData = 0;
                double totalEmissions = 0.0;
                for (Map.Entry<String, Double> entry : dateToEmissionMapData.entrySet()) {
                    daysWithData++;
                    totalEmissions += entry.getValue();
                }

                double approxAnnualEmissions = (daysWithData != 0)
                        ? totalEmissions * 0.365 / daysWithData : 0;
                gaugeAvgComparisonGlobalUserEmissions.setText(
                        String.format(Locale.getDefault(), "%.2f", approxAnnualEmissions));
                loadProgressIncrement();
            }

            @Override
            public void onFailure(@NonNull DatabaseError error) {
                Log.e("GlobalComparisonFragment",
                        "Could not fetch Eco Tracker data from Firebase.",
                        error.toException());
                loadFailed();
            }
        });
    }

    private void updateGlobalAvgEmissionDisplay(Context context) {
        try {
            GlobalAveragesCSVModel model = GlobalAveragesCSVModel.getInstance(context);
            String globalEmissionAvgTxt = String.format(Locale.getDefault(),
                    "%.2f", model.getAvgEmission("World"));
            gaugeAvgComparisonGlobalAvgEmissions.setText(globalEmissionAvgTxt);
            loadProgressIncrement();
        } catch (IOException e) {
            Toast.makeText(context,
                    "Could not access local file containing global average emissions.",
                    Toast.LENGTH_SHORT).show();
            loadFailed();
        }
    }

    private void updateOverview() {
        double userEmissions = Double.parseDouble(gaugeAvgComparisonGlobalUserEmissions.getText().toString());
        double avgEmissions = Double.parseDouble(gaugeAvgComparisonGlobalAvgEmissions.getText().toString());
        double emissionRatio = userEmissions / avgEmissions;
        if (Math.abs(emissionRatio - 1) <= 0.08) {
            gaugeAvgComparisonGlobalAvgOverview.setText("Your emissions are within the global average.");
        } else if ((emissionRatio - 1) > 0.08) {
            gaugeAvgComparisonGlobalAvgOverview.setText("Your emissions are above the global average.");
        } else {
            gaugeAvgComparisonGlobalAvgOverview.setText("Your emissions are below the global average.");
        }
    }

    private void loadProgressIncrement() {
        loadProgress++;
        if (loadProgress == PROGRESS_MAX) {
            updateOverview();
            gaugeAvgComparisonGlobalAvgProgressBar.setVisibility(View.GONE);
            gaugeAvgComparisonGlobalAvgContainer.setVisibility(View.VISIBLE);
        }
    }

    private void loadFailed() {
        loadProgress = Integer.MIN_VALUE;
        gaugeAvgComparisonGlobalAvgProgressBar.setVisibility(View.GONE);
        gaugeAvgComparisonGlobalAvgErrorMsg.setVisibility(ViewGroup.VISIBLE);
    }
}