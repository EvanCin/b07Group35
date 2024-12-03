package com.example.planetze35.EcoTrackerAllParts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planetze35.R;

import java.util.List;

public abstract class ActivityAdapterBase extends RecyclerView.Adapter<ActivityAdapterBase.ActivityViewHolder> {

    protected List<ActivityItem> activityList;

    // Constructor
    public ActivityAdapterBase(List<ActivityItem> activityList) {
        this.activityList = activityList;
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getItemLayout(), parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    @Override
    public abstract void onBindViewHolder(ActivityViewHolder holder, int position);

    protected abstract int getItemLayout();

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView activityName, co2Value;
        Button deleteButton;

        public ActivityViewHolder(View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.activity_name);
            co2Value = itemView.findViewById(R.id.co2_value);
            // deleteButton will only be used in ActivityAdapterWithButtons
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
