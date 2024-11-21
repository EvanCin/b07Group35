package com.example.planetze35;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AvgComparisonAdapter extends FragmentStateAdapter {
    public AvgComparisonAdapter(Fragment fragment){
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (position == 0) ? new GlobalComparisonFragment() : new NationalComparisonFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
