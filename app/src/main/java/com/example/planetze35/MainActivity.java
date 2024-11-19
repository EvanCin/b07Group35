package com.example.planetze35;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

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

        habitList = new ArrayList<>();
        habitList.add(new Habit("Use public transportation", "Transportation", "High"));
        habitList.add(new Habit("Recycle paper", "Consumption", "Medium"));
        habitList.add(new Habit("Eat less meat", "Food", "High"));
        habitList.add(new Habit("Install LED bulbs", "Energy", "Medium"));
        habitList.add(new Habit("Carpool", "Transportation", "High"));

        habitAdapter = new HabitAdapter(habitList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    private void filter(String text) {
        List<Habit> filteredList = new ArrayList<>();
        for (Habit habit : habitList) {
            if (habit.getName().toLowerCase().contains(text.toLowerCase()) ||
                    habit.getCategory().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(habit);
            }
        }
        habitAdapter.filterList(filteredList);
    }
}