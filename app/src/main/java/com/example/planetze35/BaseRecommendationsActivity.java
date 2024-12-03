package com.example.planetze35;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class BaseRecommendationsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    protected HabitAdapter habitAdapter;
    protected ArrayList<Habit> habitList;
    protected RecyclerView recyclerView;
    protected SearchView searchView;
    protected Button filterButton;
    protected Button backButton;

    protected HabitAdapter adoptedHabitsAdapter;
    protected HabitAdapter recommendedHabitsAdapter;
    protected ArrayList<Habit> adoptedHabitsList;
    protected ArrayList<Habit> recommendedHabitsList;
    protected RecyclerView recyclerViewUserHabits;
    protected RecyclerView recyclerViewNewHabits;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recommendations);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerViewUserHabits = findViewById(R.id.recycler_view_user_habits);
        recyclerViewNewHabits = findViewById(R.id.recycler_view_new_habits);
        searchView = findViewById(R.id.search_view);
        filterButton = findViewById(R.id.filter_button);
        backButton = findViewById(R.id.back_button);

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

        searchView.setOnQueryTextListener(this);

        filterButton.setOnClickListener(v -> showFilterDialog());

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    // Implement SearchView.OnQueryTextListener methods
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
            filterHabitsByKeyword(newText);
        }
        return false;
    }

    protected abstract void filterHabitsByKeyword(String text);

    protected abstract void resetHabitList();

    protected abstract void showFilterDialog();

    protected void createNotificationChannel() {
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
