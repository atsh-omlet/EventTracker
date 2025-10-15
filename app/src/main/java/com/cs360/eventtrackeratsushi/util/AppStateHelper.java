package com.cs360.eventtrackeratsushi.util;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class for managing persistent app state
 */
public class AppStateHelper {
    private static AppStateHelper instance;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;


    // keys for shared preferences
    private final String KEY_APP_STATE = "permissions_initial_check";
    private final String KEY_OPT_OUT_REMINDER = "opt_out_reminder";
    private final String KEY_WAS_GRANTED = "permission_was_granted";
    private final String KEY_RESCHEDULE = "reschedule_notifications";
    public static AppStateHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppStateHelper(context);
        }
        return instance;
    }

    /**
     *
     * @param context
     */
    private AppStateHelper(Context context) {
        String PREF_NAME = "AppStatePrefs";
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Gets if initial permission request was made
     * @return  true if initial permission request was made, false otherwise
     */
    public boolean isPermissionsChecked() {
        return prefs.getBoolean(KEY_APP_STATE, false);
    }

    /**
     * Sets if initial permission request was made
     */
    public void setPermissionsChecked() {
        editor.putBoolean(KEY_APP_STATE, true);
        editor.apply();
    }

    /**
     * Gets if user wishes to opt out of granting permissions
     * @return  true if user wishes to opt out, false otherwise
     */
    public boolean isOptOutReminder(){return prefs.getBoolean(KEY_OPT_OUT_REMINDER, false);}

    /**
     * Sets if user wishes to opt out of granting permissions
     */
    public void setOptOutReminder(){
        editor.putBoolean(KEY_OPT_OUT_REMINDER, true);
        editor.apply();
    }

    /**
     * Returns if permissions were granted previously
     * @return
     */
    public boolean wasPermissionGranted(){
        return prefs.getBoolean(KEY_WAS_GRANTED, false);
    }

    /**
     * Sets if permissions were granted previously
     * @param granted
     */
    public void setPermissionGranted(boolean granted){
        editor.putBoolean(KEY_WAS_GRANTED, granted);
        editor.apply();
    }

    /**
     * Returns if notifications should be rescheduled
     * @return
     */
    public boolean shouldRescheduleNotifications(){
        return prefs.getBoolean(KEY_RESCHEDULE, false);
    }

    /**
     * Sets if notifications should be rescheduled
     * @param reschedule
     */
    public void setRescheduleNotifications(boolean reschedule){
        editor.putBoolean(KEY_RESCHEDULE, reschedule);
        editor.apply();
    }
}
