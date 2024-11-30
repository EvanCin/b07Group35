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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;

public class NationalComparisonFragment extends Fragment {
    TextView gaugeAvgComparisonNationalUserEmissions;
    TextView gaugeAvgComparisonNationalAvgHeader;
    TextView gaugeAvgComparisonNationalAvgEmissions;

    public NationalComparisonFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_national_comparison, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        gaugeAvgComparisonNationalUserEmissions = view.findViewById(R.id.gaugeAvgComparisonNationalUserEmissions);
        gaugeAvgComparisonNationalAvgHeader = view.findViewById(R.id.gaugeAvgComparisonNationalAvgHeader);
        gaugeAvgComparisonNationalAvgEmissions = view.findViewById(R.id.gaugeAvgComparisonNationalAvgEmissions);

        Context context = view.getContext();

        // TODO: Actually get the country of the user here once that is implemented
        String country = "Canada";
        gaugeAvgComparisonNationalAvgHeader.setText("The average in " + country + " is");

        try {
            AvgEmissionModel model = AvgEmissionModel.getInstance(context);
            String countryEmissionAvgTxt = String.format("%.2f", model.getAvgEmission(country));
            gaugeAvgComparisonNationalAvgEmissions.setText(countryEmissionAvgTxt);
        } catch (IOException e) {
            Toast.makeText(context,
                    "Could not access local file containing global average emissions.",
                    Toast.LENGTH_SHORT).show();
        }

        // TODO: Get the user's emission average once that is implemented

//        FirebaseDatabase db = FirebaseDatabase.getInstance();
////        db.getReference().child("users/asdf").setValue(new HashMap<String, String>()).addOnCompleteListener(new OnCompleteListener<Void>() {
//        db.getReference().child("users/asdf").setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
////        db.getReference().child("users/asdf").setValue("hello").addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Log.d("Firebase Test", "Should be done? " + task.isSuccessful());
//            }
//        });
    }
}