package com.example.planetze35.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.planetze35.EcoTrackerAllParts.EcoTrackerHomepageActivity;
import com.example.planetze35.databinding.FragmentHomeBinding;
import com.example.planetze35.ecogauge.EcoGaugeActivity;
import com.example.planetze35.setup.AnnualCarbonFootprintDisplayerActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ImageButton ibEcoTracker = binding.ibEcoTracker;
        final ImageButton ibEcoGauge = binding.ibEcoGauge;
        final ImageButton ibAnnualFootprint = binding.ibAnnualFootprint;

        ibEcoTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EcoTrackerHomepageActivity.class);
                startActivity(intent);
            }
        });

        ibEcoGauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EcoGaugeActivity.class);
                startActivity(intent);
            }
        });

        ibAnnualFootprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AnnualCarbonFootprintDisplayerActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}