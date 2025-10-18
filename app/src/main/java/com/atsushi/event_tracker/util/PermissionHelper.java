package com.atsushi.event_tracker.util;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    private static final String TAG = "PermissionHelper";

    public static void requestNotificationPermission(Context context, ActivityResultLauncher<String> launcher) {
        //POST_NOTIFICATIONS Runtime Permission (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "Requesting POST_NOTIFICATIONS permission.");
                // Launch the system permission request dialog via the ActivityResultLauncher
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }


    /**
     *  checks if permissions have been granted
     * @param context
     */
    public static void requestExactAlarmSettings(Context context){

        // SCHEDULE_EXACT_ALARM Special Access Permission (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {

                Log.d(TAG, "Requesting SCHEDULE_EXACT_ALARM special access.");
                // Launch the system settings screen for special access
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                // It's crucial to set the package URI for a direct link on some devices
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Could not start exact alarm settings activity: " + e.getMessage());
                    // Fallback to general app settings if specific action fails
                    Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.getPackageName(), null));
                    context.startActivity(appSettingsIntent);
                }
            }
        }
    }

}
