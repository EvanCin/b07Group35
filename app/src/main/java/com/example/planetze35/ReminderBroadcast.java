package com.example.planetze35;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String habitName = intent.getStringExtra("habit_name");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "habitReminderChannel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Habit Reminder")
                .setContentText("Don't forget to: " + habitName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Check if permission is granted before sending the notification
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(200, builder.build());
        } else {
            // Handle the case where permission is not granted (Optional)
            // You can log the error or handle it based on your use case
        }
    }
}
