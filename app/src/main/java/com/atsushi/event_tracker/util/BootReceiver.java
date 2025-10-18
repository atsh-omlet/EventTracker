package com.atsushi.event_tracker.util;

import android.Manifest;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.atsushi.event_tracker.model.Event;
import com.atsushi.event_tracker.respository.EventRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Boot receiver class
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private final int THIRTY_MINUTES = 30 * 60 * 1000;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            return;
        }

        Log.d(TAG, "Boot completed. Re-scheduling notifications.");

        // Check for essential notification permission (API 33/Tiramisu+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Cannot re-schedule alarms after boot: POST_NOTIFICATIONS permission not granted. Notifications will be blocked.");
                return;
            }
        }

        // Check for exact alarm permission (API 31/S+) using the correct API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // Must check for null and canScheduleExactAlarms()
            if (alarmManager == null) {
                Log.e(TAG, "Cannot re-schedule alarms after boot: AlarmManager unavailable.");
                return;
            }
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e(TAG, "Cannot re-schedule alarms after boot: SCHEDULE_EXACT_ALARM permission not granted.");
                return;
            }
        }

        // Perform disk I/O off the main thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            EventRepository repository = EventRepository.getInstance(context.getApplicationContext());
            List<Event> allEvents = repository.getEventsForUser();
            DateUtils dateUtils = new DateUtils();

            long currentTime = System.currentTimeMillis();
            Log.d(TAG, "Rescheduling notifications for all events...");
            Log.d(TAG, "Current time: " + new Date(currentTime));

            for (Event event : allEvents) {
                long eventTimeMillis = dateUtils.parseDateToMillis(event.getDate());

                // Only re-schedule notifications for events that are in the future
                if (eventTimeMillis > currentTime) {
                    long notificationTime = eventTimeMillis - THIRTY_MINUTES;


                    if (notificationTime <= currentTime) {
                        notificationTime = currentTime + 5000;
                    }

                    try {
                        NotificationHelper.scheduleNotification(context, event, notificationTime);
                        Log.d(TAG, "    Re-scheduled notification for event: " + event.getTitle()
                                + " at " + new Date(notificationTime));
                    } catch (SecurityException e) {
                        Log.e(TAG, "    Failed to re-schedule notification for " + event.getTitle(), e);
                    }
                }
            }
        });
    }
}
