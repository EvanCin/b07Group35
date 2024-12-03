package com.example.planetze35;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AvgComparisonFragment extends Fragment {

    AvgComparisonAdapter avgComparisonAdapter;
    TabLayout avgComparisonTabLayout;
    ViewPager2 viewPager;

    public AvgComparisonFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_avg_comparison, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        avgComparisonAdapter = new AvgComparisonAdapter(this);
        viewPager = view.findViewById(R.id.gaugeAvgComparisonPager);
        viewPager.setAdapter(avgComparisonAdapter);
        avgComparisonTabLayout = view.findViewById(R.id.gaugeAvgComparisonTabLayout);
        new TabLayoutMediator(avgComparisonTabLayout, viewPager, (tab, position) -> {
            String tabTitle = "";
            switch (position) {
                case 0:
                    tabTitle = "GLOBAL";
                    break;
                case 1:
                    tabTitle = "NATIONAL";
                    break;
                default:
                    break;
            }
            tab.setText(tabTitle);
        }).attach();
    }
}