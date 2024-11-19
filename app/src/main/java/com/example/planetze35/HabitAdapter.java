package com.example.planetze35;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private final List<Habit> habitList;

    public HabitAdapter(List<Habit> habitList) {
        this.habitList = habitList;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.habitName.setText(habit.getName());
        holder.habitCategory.setText(habit.getCategory());
        holder.habitImpact.setText(habit.getImpact());
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public void filterList(List<Habit> filteredList) {
        int oldSize = habitList.size();
        int newSize = filteredList.size();


        habitList.clear();
        habitList.addAll(filteredList);

        if (newSize < oldSize) {
            notifyItemRangeRemoved(newSize, oldSize - newSize);
        } else if (newSize > oldSize) {
            notifyItemRangeInserted(oldSize, newSize - oldSize);
        } else {
            for (int i = 0; i < newSize; i++) {
                notifyItemChanged(i);
            }
        }
    }


    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitName, habitCategory, habitImpact;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habit_name);
            habitCategory = itemView.findViewById(R.id.habit_category);
            habitImpact = itemView.findViewById(R.id.habit_impact);
        }
    }
}
