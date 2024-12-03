package com.example.planetze35.EcoTrackerAllParts;

import android.view.View;

import com.example.planetze35.R;

import java.util.List;

public class ActivityAdapterWithButtons extends ActivityAdapterBase {

    private ActivityClickListener activityClickListener;

    // Constructor
    public ActivityAdapterWithButtons(List<ActivityItem> activityList, ActivityClickListener activityClickListener) {
        super(activityList);
        this.activityClickListener = activityClickListener;
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        ActivityItem activityItem = activityList.get(position);

        // Set data into the views
        holder.activityName.setText(activityItem.getActivityName());
        holder.co2Value.setText(activityItem.getCo2Value());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityClickListener != null) {
                    activityClickListener.onDeleteClick(activityItem);
                }
            }
        });
    }

    @Override
    protected int getItemLayout() {
        return R.layout.activity_item; // Layout with delete button
    }

    // Interface to handle delete button clicks
    public interface ActivityClickListener {
        void onDeleteClick(ActivityItem activityItem);
    }
}
