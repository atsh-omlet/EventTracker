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


    private final String KEY_APP_STATE = "permissions_initial_check";

    public static AppStateHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppStateHelper(context);
        }
        return instance;
    }

    private AppStateHelper(Context context) {
        String PREF_NAME = "AppStatePrefs";
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public boolean isPermissionsChecked() {
        return prefs.getBoolean(KEY_APP_STATE, false);
    }
    public void setPermissionsChecked() {
        editor.putBoolean(KEY_APP_STATE, true);
        editor.apply();
    }
}
