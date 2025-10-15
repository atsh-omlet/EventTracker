package com.cs360.eventtrackeratsushi.util;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.cs360.eventtrackeratsushi.model.Event;

import java.util.Date;

/**
 * Helper class for scheduling and canceling notifications
 */
public class NotificationHelper {
    private static final String TAG = "NotificationHelper";


    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    public static void scheduleNotification(Context context, Event event, long notificationTime) {
        DateUtils dateUtils = new DateUtils();

        Log.d(TAG, "Scheduling notification for event: " + event.getTitle());
        Log.d(TAG, "Notification time: " + new Date(notificationTime));
        Log.d(TAG, "Current time: " + new Date(System.currentTimeMillis()));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d(TAG, "Exact alarm scheduling is DENIED. Cannot schedule notification.");
                return;
            }
        }
        // Permission is either granted or not needed
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra("title", event.getTitle());
        intent.putExtra("eventId", event.getId());
        intent.putExtra("eventDate", dateUtils.formatTime(event.getDate()));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                event.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        try {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime,
                    pendingIntent
            );
        } catch (SecurityException e) {
                Log.e(TAG, "SecurityException: " + e.getMessage());
        }
    }

    public static void cancelNotification(Context context, int eventId) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, eventId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d(TAG, "Notification canceled for event ID: " + eventId);
        }
    }



}
