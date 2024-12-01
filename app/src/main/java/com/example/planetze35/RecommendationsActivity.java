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

import com.example.planetze35.data.UserDataHandler;

import java.util.ArrayList;
import java.util.List;

public class RecommendationsActivity extends AppCompatActivity {

    private HabitAdapter habitAdapter;
    private List<Habit> habitList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        SearchView searchView = findViewById(R.id.search_view);
        Button filterButton = findViewById(R.id.filter_button);
        Button backButton = findViewById(R.id.back_button);  // Initialize the back button here

        // Initialize habit list and add habits
        habitList = new ArrayList<>();



        // Transportation Habits
        habitList.add(new Habit("Drive Personal Vehicle (Gasoline)", "Transportation", "High"));
        habitList.add(new Habit("Drive Personal Vehicle (Electric)", "Transportation", "Low"));
        habitList.add(new Habit("Take Public Transportation (Bus)", "Transportation", "Low"));
        habitList.add(new Habit("Take Public Transportation (Train)", "Transportation", "Medium"));
        habitList.add(new Habit("Cycling or Walking", "Transportation", "Low"));
        habitList.add(new Habit("Flight (Short Haul)", "Transportation", "High"));

        // Food Habits
        habitList.add(new Habit("Beef Consumption", "Food", "High"));
        habitList.add(new Habit("Plant-Based Meal", "Food", "Low"));

        // Consumption Habits
        habitList.add(new Habit("Buy New Clothes", "Consumption", "High"));
        habitList.add(new Habit("Buy Electronics (Smartphone)", "Consumption", "Medium"));
        habitList.add(new Habit("Buy Electronics (Laptop)", "Consumption", "High"));

        // Energy Bills Habits
        habitList.add(new Habit("Electricity Usage", "Energy", "Medium"));
        habitList.add(new Habit("Gas Usage", "Energy", "Medium"));

        // Set up RecyclerView with Adapter
        habitAdapter = new HabitAdapter(new ArrayList<>(habitList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        // Create Notification Channel
        createNotificationChannel();

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Set up the SearchView to filter habits by keyword
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterByKeyword(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    resetHabitList();
                } else {
                    filterByKeyword(newText);
                }
                return false;
            }
        });

        // Set up the filter button to show filter options
        filterButton.setOnClickListener(v -> showFilterDialog());

        // Set up the back button to go to the previous screen
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());



    }

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
