package com.example.planetze35;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class GlobalComparisonFragment extends Fragment {

    TextView gaugeAvgComparisonGlobalUserEmissions;
    TextView gaugeAvgComparisonGlobalAvgEmissions;

    public GlobalComparisonFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_global_comparison, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        gaugeAvgComparisonGlobalUserEmissions = view.findViewById(R.id.gaugeAvgComparisonNationalUserEmissions);
        gaugeAvgComparisonGlobalAvgEmissions = view.findViewById(R.id.gaugeAvgComparisonGlobalAvgEmissions);

        Context context = view.getContext();

        try {
            AvgEmissionModel model = AvgEmissionModel.getInstance(context);
            String globalEmissionAvgTxt = String.format("%.2f", model.getAvgEmission("World"));
            gaugeAvgComparisonGlobalAvgEmissions.setText(globalEmissionAvgTxt);
        } catch (IOException e) {
            Toast.makeText(context,
                    "Could not access local file containing global average emissions.",
                    Toast.LENGTH_SHORT).show();
        }

        // TODO: Get the user's emission average once that is implemented
    }
}