package com.cs360.eventtrackeratsushi.util;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.respository.EventRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private final int THIRTY_MINUTES = 30 * 60 * 1000;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Boot completed. Re-scheduling notifications.");

            // Perform disk I/O off the main thread
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                EventRepository repository = EventRepository.getInstance(context.getApplicationContext());
                List<Event> allEvents = repository.getEventsForUser();
                DateUtils dateUtils = new DateUtils();

                long currentTime = System.currentTimeMillis();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "Cannot re-schedule alarms after boot: SCHEDULE_EXACT_ALARM permission not granted.");
                        return;
                    }
                }


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
                            Log.d(TAG, "Re-scheduled notification for event: " + event.getTitle());
                        } catch (SecurityException e) {
                            Log.e(TAG, "Failed to re-schedule notification for " + event.getTitle(), e);
                        }
                    }
                }
            });
        }
    }
}
