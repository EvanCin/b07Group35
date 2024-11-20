package com.example.planetze35;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private HabitAdapter habitAdapter;
    private List<Habit> habitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        SearchView searchView = findViewById(R.id.search_view);
        Button filterButton = findViewById(R.id.filter_button);

        // Initialize habit list and add habits
        habitList = new ArrayList<>();
        habitList.add(new Habit("Use public transportation", "Transportation", "High"));
        habitList.add(new Habit("Recycle paper", "Consumption", "Medium"));
        habitList.add(new Habit("Eat less meat", "Food", "High"));
        habitList.add(new Habit("Install LED bulbs", "Energy", "Medium"));
        habitList.add(new Habit("Carpool", "Transportation", "High"));

        // Initialize the adapter and set it to the RecyclerView
        habitAdapter = new HabitAdapter(new ArrayList<>(habitList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        // Create notification channel
        createNotificationChannel();

        // Check and request notification permission if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Set up the SearchView to filter the habits by keyword
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterByKeyword(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    // If the search text is empty, reset the list to show all items
                    resetHabitList();
                } else {
                    // Otherwise, filter the list based on the entered text
                    filterByKeyword(newText);
                }
                return false;
            }
        });

        // Set up the filter button to show filter options
        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    // Method to create a notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HabitReminderChannel";
            String description = "Channel for Habit Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("habitReminderChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Show filter options in a dialog
    private void showFilterDialog() {
        String[] filterOptions = {"Filter by Category", "Filter by Impact"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a filter")
                .setItems(filterOptions, (dialog, which) -> {
                    if (which == 0) {
                        filterByCategory();
                    } else if (which == 1) {
                        filterByImpact();
                    }
                })
                .show();
    }

    private void filterByKeyword(String text) {
        List<Habit> filteredList = new ArrayList<>();
        for (Habit habit : habitList) {
            if (habit.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(habit);
            }
        }
        habitAdapter.filterList(filteredList);
    }

    private void resetHabitList() {
        habitAdapter.filterList(new ArrayList<>(habitList));
    }

    private void filterByCategory() {
        String[] categories = {"Transportation", "Energy", "Food", "Consumption"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category")
                .setItems(categories, (dialog, which) -> {
                    String selectedCategory = categories[which];
                    List<Habit> filteredList = new ArrayList<>();
                    for (Habit habit : habitList) {
                        if (habit.getCategory().equalsIgnoreCase(selectedCategory)) {
                            filteredList.add(habit);
                        }
                    }
                    habitAdapter.filterList(filteredList);
                })
                .show();
    }

    private void filterByImpact() {
        String[] impacts = {"High", "Medium", "Low"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Impact Level")
                .setItems(impacts, (dialog, which) -> {
                    String selectedImpact = impacts[which];
                    List<Habit> filteredList = new ArrayList<>();
                    for (Habit habit : habitList) {
                        if (habit.getImpact().equalsIgnoreCase(selectedImpact)) {
                            filteredList.add(habit);
                        }
                    }
                    habitAdapter.filterList(filteredList);
                })
                .show();
    }
}
