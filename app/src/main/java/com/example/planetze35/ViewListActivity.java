package com.example.planetze35;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ViewListActivity extends AppCompatActivity implements ActivityAdapterWithButtons.ActivityClickListener {

    private RecyclerView recyclerView;
    private ActivityAdapterWithButtons adapter;
    private List<ActivityItem> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.activities_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Example list of activities
        activityList = new ArrayList<>();
        activityList.add(new ActivityItem("Driving", "5.0 kg CO2e"));
        activityList.add(new ActivityItem("Eating Beef", "2.0 kg CO2e"));

        // Set up the adapter
        adapter = new ActivityAdapterWithButtons(activityList, this);
        recyclerView.setAdapter(adapter);
    }

    // Handle the Edit button click
    @Override
    public void onEditClick(ActivityItem activityItem) {
        Toast.makeText(this, "Edit clicked for: " + activityItem.getActivityName(), Toast.LENGTH_SHORT).show();
    }

    // Handle the Delete button click
    @Override
    public void onDeleteClick(ActivityItem activityItem) {
        // Remove the activity from the list and notify the adapter
        activityList.remove(activityItem);
        adapter.notifyDataSetChanged();  // Refresh the RecyclerView

        Toast.makeText(this, "Deleted: " + activityItem.getActivityName(), Toast.LENGTH_SHORT).show();
    }
}
