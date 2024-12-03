package com.example.planetze35;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class FilterHelper {

    public static void showFilterDialog(Context context, String[] filterOptions, Filterable filterable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a filter")
                .setItems(filterOptions, (dialog, which) -> {
                    if (which == 0) {
                        filterable.filterByCategory();
                    } else if (which == 1) {
                        filterable.filterByImpact();
                    }
                })
                .show();
    }

    public static void filterHabitsByCategory(Context context, String[] categories, List<Habit> habitList, HabitAdapter habitAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    public static void filterHabitsByImpact(Context context, String[] impacts, List<Habit> habitList, HabitAdapter habitAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
}
