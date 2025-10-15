package com.cs360.eventtrackeratsushi.util;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import androidx.core.app.NotificationCompat;

import com.cs360.eventtrackeratsushi.R;
import com.cs360.eventtrackeratsushi.ui.MainActivity;

/**
 * Broadcast receiver for handling notifications
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "event_channel";
    private static final String CHANNEL_NAME = "Event Reminders";

    private void createNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for upcoming events");
            channel.enableLights(true);
            channel.setLightColor(android.graphics.Color.BLUE);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("eventDate");
        int eventId = intent.getIntExtra("eventId", -1);

        // Create intent to open app when notification is clicked
        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, eventId, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "event_channel";

        SessionManager sessionManager = SessionManager.getInstance(context);
        String userName = sessionManager.getUsername();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Upcoming Event for " + userName)
                .setContentText("Your event \"" + title + "\" is starting soon at " + date + "!")
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        if (manager != null) {
            manager.notify(eventId, builder.build());
        }
    }
}