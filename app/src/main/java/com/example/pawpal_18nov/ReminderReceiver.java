package com.example.pawpal_18nov;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve task name from the Intent
        String taskName = intent.getStringExtra("taskName");
        if (taskName == null || taskName.isEmpty()) {
            taskName = "Unnamed Task";
        }

        // Get the NotificationManager system service
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification channel ID
        String channelId = "ReminderChannel";

        // Create the notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for task reminders");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build the notification
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Task Reminder")
                .setContentText("Reminder for: " + taskName)
                .setSmallIcon(R.drawable.ic_notification) // Replace with your app's icon
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // Dismiss the notification when clicked
                .build();

        // Notify the user
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notification);
        }
    }
}
