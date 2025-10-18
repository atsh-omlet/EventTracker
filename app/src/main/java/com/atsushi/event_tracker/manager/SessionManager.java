package com.atsushi.event_tracker.manager;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Session manager class for managing user login sessions
 */
public class SessionManager {
    // Constants for SharedPreferences keys
    private final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private final String KEY_USERNAME = "username";
    private final String KEY_USERID = "userId";

    private static SessionManager instance;

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    private SessionManager(Context context) {
        String PREF_NAME = "LoginPrefs";
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Saves the login session details to SharedPreferences.
     * @param userId The user ID.
     * @param username The username.
     */
    public void saveLoginSession(int userId, String username){
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USERID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    /**
     * Clears the login session details from SharedPreferences.
     */
    public void clearSession() {
        editor.clear().apply();
    }

    /**
     * Checks if the user is currently logged in.
     * @return True if the user is logged in, false otherwise.
     */
    public boolean isLoggedIn(){
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Retrieves the user ID from SharedPreferences.
     * @return The user ID, or -1 if not found.
     */
    public int getUserId(){
        return prefs.getInt(KEY_USERID, -1);
    }

    /**
     * Retrieves the username from SharedPreferences.
     * @return The username, or null if not found.
     */
    public String getUsername(){
        return prefs.getString(KEY_USERNAME, null);
    }


}
