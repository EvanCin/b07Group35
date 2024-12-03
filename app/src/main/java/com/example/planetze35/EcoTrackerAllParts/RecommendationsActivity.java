package com.example.planetze35.EcoTrackerAllParts;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planetze35.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecommendationsActivity extends BaseRecommendationsActivity implements Filterable {

    private HabitAdapter adoptedHabitsAdapter;
    private HabitAdapter recommendedHabitsAdapter;
    private List<Habit> adoptedHabitsList;
    private List<Habit> recommendedHabitsList;

    private RecyclerView recyclerViewUserHabits;
    private RecyclerView recyclerViewNewHabits;
    private TextView adoptedHabitsDivider;
    private TextView newHabitsDivider;

    public interface FirebaseCallback {
        void onDataFetched(Map<String, Integer> categoryCounts);
        void onError(Exception e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerViewUserHabits = findViewById(R.id.recycler_view_user_habits);
        recyclerViewNewHabits = findViewById(R.id.recycler_view_new_habits);
        searchView = findViewById(R.id.search_view);
        filterButton = findViewById(R.id.filter_button);
        backButton = findViewById(R.id.back_button);

        adoptedHabitsDivider = findViewById(R.id.user_adopted_habits_divider);
        newHabitsDivider = findViewById(R.id.new_habits_divider);

        habitList = new ArrayList<>();
        adoptedHabitsList = new ArrayList<>();
        recommendedHabitsList = new ArrayList<>();

        habitAdapter = new HabitAdapter(habitList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        adoptedHabitsAdapter = new HabitAdapter(adoptedHabitsList);
        recyclerViewUserHabits.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUserHabits.setAdapter(adoptedHabitsAdapter);

        recommendedHabitsAdapter = new HabitAdapter(recommendedHabitsList);
        recyclerViewNewHabits.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNewHabits.setAdapter(recommendedHabitsAdapter);

        // Check user login status
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : null;

        if (userId == null) {
            setupNonLoggedInUser();
        } else {
            setupLoggedInUser(userId);
        }

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupNonLoggedInUser() {
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewUserHabits.setVisibility(View.GONE);
        recyclerViewNewHabits.setVisibility(View.GONE);
        adoptedHabitsDivider.setVisibility(View.GONE);
        newHabitsDivider.setVisibility(View.GONE);
        habitList = new ArrayList<>();

        initializeStaticHabits();

        habitAdapter = new HabitAdapter(new ArrayList<>(habitList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);
        habitAdapter = new HabitAdapter(new ArrayList<>(habitList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        searchView.setOnQueryTextListener(this);
        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void setupLoggedInUser(String userId) {
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
        habitAdapter = new HabitAdapter(new ArrayList<>(habitList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);
        habitAdapter = new HabitAdapter(new ArrayList<>(habitList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        // Set up search and filter for adopted habits
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAdoptedHabitsByKeyword(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    resetAdoptedHabitList();
                } else {
                    filterAdoptedHabitsByKeyword(newText);
                }
                return false;
            }
        });

        filterButton.setOnClickListener(v -> showFilterDialogForAdoptedHabits());
    }

    private void initializeStaticHabits() {
        habitList.clear();

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

        habitAdapter.updateHabitList(habitList);
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
        adoptedHabitsAdapter.updateHabitList(adoptedHabitsList);
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

        recommendedHabitsAdapter.updateHabitList(recommendedHabitsList);
    }

    // Methods for non-logged-in users
    @Override
    protected void filterHabitsByKeyword(String text) {
        List<Habit> filteredList = new ArrayList<>();
        for (Habit habit : habitList) {
            if (habit.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(habit);
            }
        }
        habitAdapter.updateHabitList(filteredList);
    }

    @Override
    protected void resetHabitList() {
        habitAdapter.updateHabitList(new ArrayList<>(habitList));
    }

    @Override
    protected void showFilterDialog() {
        String[] filterOptions = {"Filter by Category", "Filter by Impact"};
        FilterHelper.showFilterDialog(this, filterOptions, this);
    }

    @Override
    public void filterByCategory() {
        String[] categories = {"Transportation", "Energy", "Food", "Consumption"};
        FilterHelper.filterHabitsByCategory(this, categories, habitList, habitAdapter);
    }

    @Override
    public void filterByImpact() {
        String[] impacts = {"High", "Medium", "Low"};
        FilterHelper.filterHabitsByImpact(this, impacts, habitList, habitAdapter);
    }

    // Methods for logged-in users
    private void filterAdoptedHabitsByKeyword(String text) {
        List<Habit> filteredList = new ArrayList<>();
        for (Habit habit : adoptedHabitsList) {
            if (habit.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(habit);
            }
        }
        adoptedHabitsAdapter.updateHabitList(filteredList);
    }

    private void resetAdoptedHabitList() {
        adoptedHabitsAdapter.updateHabitList(new ArrayList<>(adoptedHabitsList));
    }

    private void showFilterDialogForAdoptedHabits() {
        String[] filterOptions = {"Filter by Category", "Filter by Impact"};
        FilterHelper.showFilterDialog(this, filterOptions, new Filterable() {
            @Override
            public void filterByCategory() {
                filterAdoptedHabitsByCategory();
            }

            @Override
            public void filterByImpact() {
                filterAdoptedHabitsByImpact();
            }
        });
    }

    private void filterAdoptedHabitsByCategory() {
        String[] categories = {"Transportation", "Energy", "Food", "Consumption"};
        FilterHelper.filterHabitsByCategory(this, categories, adoptedHabitsList, adoptedHabitsAdapter);
    }

    private void filterAdoptedHabitsByImpact() {
        String[] impacts = {"High", "Medium", "Low"};
        FilterHelper.filterHabitsByImpact(this, impacts, adoptedHabitsList, adoptedHabitsAdapter);
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
}
