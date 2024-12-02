package com.example.planetze35;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecommendationsActivity extends AppCompatActivity {

    private HabitAdapter adoptedHabitsAdapter;
    private HabitAdapter recommendedHabitsAdapter;
    private List<Habit> adoptedHabitsList;
    private List<Habit> recommendedHabitsList;
    private HabitAdapter habitAdapter;
    private List<Habit> habitList;

    public interface FirebaseCallback {
        void onDataFetched(Map<String, Integer> categoryCounts);
        void onError(Exception e);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView recyclerViewUserHabits = findViewById(R.id.recycler_view_user_habits);
        RecyclerView recyclerViewNewHabits = findViewById(R.id.recycler_view_new_habits);
        SearchView searchView = findViewById(R.id.search_view);
        Button filterButton = findViewById(R.id.filter_button);
        Button backButton = findViewById(R.id.back_button);

        TextView adoptedHabitsDivider = findViewById(R.id.user_adopted_habits_divider);
        TextView newHabitsDivider = findViewById(R.id.new_habits_divider);

        adoptedHabitsList = new ArrayList<>();
        recommendedHabitsList = new ArrayList<>();
        habitList = new ArrayList<>();

        // Initialize adapters before using them
        adoptedHabitsAdapter = new HabitAdapter(adoptedHabitsList);
        recyclerViewUserHabits.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUserHabits.setAdapter(adoptedHabitsAdapter);

        recommendedHabitsAdapter = new HabitAdapter(recommendedHabitsList);
        recyclerViewNewHabits.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNewHabits.setAdapter(recommendedHabitsAdapter);

        habitAdapter = new HabitAdapter(habitList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : null; // Set to null if not logged in

        if (userId == null) {
            // Non-logged-in user
            recyclerView.setVisibility(View.VISIBLE);
            recyclerViewUserHabits.setVisibility(View.GONE);
            recyclerViewNewHabits.setVisibility(View.GONE);
            adoptedHabitsDivider.setVisibility(View.GONE);
            newHabitsDivider.setVisibility(View.GONE);

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

            habitAdapter = new HabitAdapter(new ArrayList<>(habitList));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(habitAdapter);
            habitAdapter = new HabitAdapter(new ArrayList<>(habitList));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(habitAdapter);

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

            filterButton.setOnClickListener(v -> showFilterDialog());
        }

        else {
                // Logged-in user
                recyclerView.setVisibility(View.GONE);
                recyclerViewUserHabits.setVisibility(View.VISIBLE);
                recyclerViewNewHabits.setVisibility(View.VISIBLE);
                adoptedHabitsDivider.setVisibility(View.VISIBLE);
                newHabitsDivider.setVisibility(View.VISIBLE);

                FetchUserDailyActivities fetcher = new FetchUserDailyActivities();
            fetcher.fetchUserActivities(userId, new FirebaseCallback() {
                @Override
                public void onDataFetched(Map<String, Integer> categoryCounts) {
                    displayUserHabits(categoryCounts);
                    displayRecommendedHabits(categoryCounts);
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(RecommendationsActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    initializeStaticHabits();
                    adoptedHabitsAdapter.updateHabitList(adoptedHabitsList);
                    recommendedHabitsAdapter.updateHabitList(recommendedHabitsList);
                }
            });

            // Set up search and filter for adoptedHabitsAdapter
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterHabitsByKeyword(query);
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

            filterButton.setOnClickListener(v -> showFilterDialogForHabits());
        }

        createNotificationChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

    }
    private void showFilterDialogForHabits() {
        String[] filterOptions = {"Filter by Category", "Filter by Impact"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a filter")
                .setItems(filterOptions, (dialog, which) -> {
                    if (which == 0) {
                        filterHabitsByCategory();
                    } else if (which == 1) {
                        filterHabitsByImpact();
                    }
                })
                .show();
    }

    private void filterHabitsByCategory() {
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
                    habitAdapter.updateHabitList(filteredList);
                })
                .show();
    }
    private void filterHabitsByImpact() {
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
                    habitAdapter.updateHabitList(filteredList);
                })
                .show();
    }
    private void initializeStaticHabits() {
        habitList.clear();

        // Transportation Habits
        adoptedHabitsList.add(new Habit("Drive Personal Vehicle (Gasoline)", "Transportation", "High"));
        adoptedHabitsList.add(new Habit("Drive Personal Vehicle (Electric)", "Transportation", "Low"));
        adoptedHabitsList.add(new Habit("Take Public Transportation (Bus)", "Transportation", "Low"));
        adoptedHabitsList.add(new Habit("Take Public Transportation (Train)", "Transportation", "Medium"));
        adoptedHabitsList.add(new Habit("Cycling or Walking", "Transportation", "Low"));
        adoptedHabitsList.add(new Habit("Flight (Short Haul)", "Transportation", "High"));

        // Food Habits
        adoptedHabitsList.add(new Habit("Beef Consumption", "Food", "High"));
        adoptedHabitsList.add(new Habit("Plant-Based Meal", "Food", "Low"));

        // Consumption Habits
        adoptedHabitsList.add(new Habit("Buy New Clothes", "Consumption", "High"));
        adoptedHabitsList.add(new Habit("Buy Electronics (Smartphone)", "Consumption", "Medium"));
        adoptedHabitsList.add(new Habit("Buy Electronics (Laptop)", "Consumption", "High"));

        // Energy Bills Habits
        adoptedHabitsList.add(new Habit("Electricity Usage", "Energy", "Medium"));
        adoptedHabitsList.add(new Habit("Gas Usage", "Energy", "Medium"));

        habitAdapter.updateHabitList(habitList);


    }
    private void filterHabitsByKeyword(String text) {
        List<Habit> filteredList = new ArrayList<>();
        for (Habit habit : habitList) {
            if (habit.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(habit);
            }
        }
        habitAdapter.updateHabitList(filteredList);
    }



    private void displayUserHabits(Map<String, Integer> categoryCounts) {
        adoptedHabitsList.clear();
        // Add user's adopted habits based on fetched data
        for (String category : categoryCounts.keySet()) {
            if ("Transportation".equalsIgnoreCase(category)) {
                adoptedHabitsList.add(new Habit("Cycling or Walking", "Transportation", "Low"));
                adoptedHabitsList.add(new Habit("Take Public Transportation (Bus)", "Transportation", "Low"));
            } else if ("Food".equalsIgnoreCase(category)) {
                adoptedHabitsList.add(new Habit("Plant-Based Meal", "Food", "Low"));
            } else if ("Consumption".equalsIgnoreCase(category)) {
                adoptedHabitsList.add(new Habit("Buy New Clothes", "Consumption", "High"));
            } else if ("Energy".equalsIgnoreCase(category)) {
                adoptedHabitsList.add(new Habit("Electricity Usage", "Energy", "Medium"));
            }
        }
        adoptedHabitsAdapter.filterList(adoptedHabitsList);
    }

    private void displayRecommendedHabits(Map<String, Integer> categoryCounts) {
        recommendedHabitsList.clear();
        String mostFrequentCategory = null;
        int maxCount = 0;

        // Find the most frequent category to recommend habits accordingly
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostFrequentCategory = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        // Recommend new habits based on the most frequent category
        if ("Transportation".equalsIgnoreCase(mostFrequentCategory)) {
            recommendedHabitsList.add(new Habit("Carpool to reduce emissions", "Transportation", "Medium"));
        } else if ("Food".equalsIgnoreCase(mostFrequentCategory)) {
            recommendedHabitsList.add(new Habit("Switch to plant-based meals", "Food", "Low"));
        } else if ("Consumption".equalsIgnoreCase(mostFrequentCategory)) {
            recommendedHabitsList.add(new Habit("Avoid fast fashion purchases", "Consumption", "High"));
        } else if ("Energy".equalsIgnoreCase(mostFrequentCategory)) {
            recommendedHabitsList.add(new Habit("Install energy-efficient appliances", "Energy", "Medium"));
        }

        recommendedHabitsAdapter.filterList(recommendedHabitsList);
    }

    public void createNotificationChannel() {
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
        for (Habit habit : adoptedHabitsList) {
            if (habit.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(habit);
            }
        }
        adoptedHabitsAdapter.filterList(filteredList);
    }

    private void resetHabitList() {
        adoptedHabitsAdapter.filterList(new ArrayList<>(adoptedHabitsList));
    }

    private void filterByCategory() {
        String[] categories = {"Transportation", "Energy", "Food", "Consumption"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category")
                .setItems(categories, (dialog, which) -> {
                    String selectedCategory = categories[which];
                    List<Habit> filteredList = new ArrayList<>();
                    for (Habit habit : adoptedHabitsList) {
                        if (habit.getCategory().equalsIgnoreCase(selectedCategory)) {
                            filteredList.add(habit);
                        }
                    }
                    adoptedHabitsAdapter.filterList(filteredList);
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
                    for (Habit habit : adoptedHabitsList) {
                        if (habit.getImpact().equalsIgnoreCase(selectedImpact)) {
                            filteredList.add(habit);
                        }
                    }
                    adoptedHabitsAdapter.filterList(filteredList);
                })
                .show();
    }



}

