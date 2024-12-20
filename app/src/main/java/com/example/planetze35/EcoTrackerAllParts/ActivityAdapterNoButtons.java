package com.example.planetze35.EcoTrackerAllParts;

import com.example.planetze35.R;

import java.util.List;

public class ActivityAdapterNoButtons extends ActivityAdapterBase {

    // Constructor
    public ActivityAdapterNoButtons(List<ActivityItem> activityList) {
        super(activityList);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        ActivityItem activityItem = activityList.get(position);

        // Set data into the views
        holder.activityName.setText(activityItem.getActivityName());
        holder.co2Value.setText(activityItem.getCo2Value());
    }

    @Override
    protected int getItemLayout() {
        return R.layout.activity_item_no_buttons; // Layout without edit and delete buttons
    }
}
