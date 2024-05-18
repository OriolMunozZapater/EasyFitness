package com.uablis.easyfitness;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "ObjectiveUpdateChannel";
    public static final String DAILY_CHANNEL_ID = "DailyExerciseReminderChannel";
    public static final String ACTION_OBJECTIVE_REMINDER = "com.uablis.easyfitness.ACTION_OBJECTIVE_REMINDER";
    public static final String ACTION_DAILY_REMINDER = "com.uablis.easyfitness.ACTION_DAILY_REMINDER";

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        String action = intent.getAction();
        Log.d("NotificationReceiver", "Received action: " + action); // Log para depuración

        if (ACTION_OBJECTIVE_REMINDER.equals(action)) {
            sendObjectiveReminder(context);
        } else if (ACTION_DAILY_REMINDER.equals(action)) {
            sendDailyReminder(context);
        } else {
            Log.e("NotificationReceiver", "Unknown action received: " + action);
        }
    }

    private void sendObjectiveReminder(Context context) {
        Intent notificationIntent = new Intent(context, ObjectiveDetailsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Cambia esto por el ícono de tu aplicación
                .setContentTitle("Update Your Weight")
                .setContentText("Remember to update your current weight progress!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
        Log.d("NotificationReceiver", "Objective reminder notification sent");
    }

    private void sendDailyReminder(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class); // O la actividad principal de tu aplicación
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DAILY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Cambia esto por el ícono de tu aplicación
                .setContentTitle("Time to Exercise!")
                .setContentText("Don't forget to do your daily exercise!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
        Log.d("NotificationReceiver", "Daily reminder notification sent");
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel objectiveChannel = new NotificationChannel(
                    CHANNEL_ID, "Objective Update Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            objectiveChannel.setDescription("Channel for objective update reminders");

            NotificationChannel dailyChannel = new NotificationChannel(
                    DAILY_CHANNEL_ID, "Daily Exercise Reminder Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            dailyChannel.setDescription("Channel for daily exercise reminders");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(objectiveChannel);
            notificationManager.createNotificationChannel(dailyChannel);

            Log.d("NotificationReceiver", "Notification channels created");
        }
    }
}
