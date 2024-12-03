package com.example.planetze35.EcoTrackerAllParts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DiffUtil;

import com.example.planetze35.R;

import java.util.Calendar;
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
    public void updateHabitList(List<Habit> newHabitList) {
        this.habitList.clear();
        this.habitList.addAll(newHabitList);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.habitName.setText(habit.getName());
        holder.habitCategory.setText(habit.getCategory());
        holder.habitImpact.setText(habit.getImpact());

        int iconResId = getIconForCategory(habit.getCategory());
        holder.habitIcon.setImageResource(iconResId);

        holder.reminderButton.setOnClickListener(v ->
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Set Reminder")
                    .setMessage("Do you want to set a reminder for \"" + habit.getName() + "\"?").setPositiveButton("Yes", (dialog, which) -> setReminder(holder, position, habit))
                    .setPositiveButton("Yes", (dialog, which) -> setReminder(holder, position, habit))

                    .setNegativeButton("No", null)
                    .show()
        );
    }

    private void setReminder(@NonNull HabitViewHolder holder, int position, Habit habit) {
        AlarmManager alarmManager = (AlarmManager) holder.itemView.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(holder.itemView.getContext(), ReminderBroadcast.class);
        intent.putExtra("habit_name", habit.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(holder.itemView.getContext(), position, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the time for the alarm
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9); // e.g., Set reminder time to 9 AM
        calendar.set(Calendar.MINUTE, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            Toast.makeText(holder.itemView.getContext(), "Daily reminder set for: " + habit.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public void filterList(List<Habit> filteredList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return habitList.size();
            }

            @Override
            public int getNewListSize() {
                return filteredList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return habitList.get(oldItemPosition).getName().equals(filteredList.get(newItemPosition).getName());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return habitList.get(oldItemPosition).equals(filteredList.get(newItemPosition));
            }
        });

        habitList.clear();
        habitList.addAll(filteredList);
        diffResult.dispatchUpdatesTo(this);
    }

    private int getIconForCategory(String category) {
        switch (category.toLowerCase()) {
            case "transportation":
                return R.drawable.baseline_airport_shuttle_24;
            case "energy":
                return R.drawable.baseline_ac_unit_24;
            case "food":
                return R.drawable.baseline_bakery_dining_24;
            case "consumption":
                return R.drawable.baseline_add_shopping_cart_24;
            default:
                return R.drawable.ic_launcher_foreground;
        }
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitName, habitCategory, habitImpact;
        ImageView habitIcon;
        ImageButton reminderButton;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habit_name);
            habitCategory = itemView.findViewById(R.id.habit_category);
            habitImpact = itemView.findViewById(R.id.habit_impact);
            habitIcon = itemView.findViewById(R.id.habit_icon);
            reminderButton = itemView.findViewById(R.id.reminder_button);
        }
    }
}
