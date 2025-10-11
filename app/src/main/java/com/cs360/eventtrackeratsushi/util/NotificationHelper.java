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

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra("title", event.getTitle());
        intent.putExtra("eventId", event.getId());
        intent.putExtra("eventDate", dateUtils.formatDate(event.getDate()));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                event.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            notificationTime,
                            pendingIntent
                    );
                    Log.d(TAG, "Notification scheduled for event: " + event.getTitle());
                } else {
                    Intent intent2 = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent2);
                    Log.e(TAG, "Exact alarm scheduling not available");
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notificationTime,
                        pendingIntent
                );
                Log.d(TAG, "Notification scheduled for event (pre-Android S): " + event.getTitle());
            }
        } catch (SecurityException e){
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
        }
    }

}
