package com.cs360.eventtrackeratsushi.util;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
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

    public void saveLoginSession(int userId, String username){
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USERID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public void clearSession() {
        editor.clear().apply();
    }

    public boolean isLoggedIn(){
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId(){
        return prefs.getInt(KEY_USERID, -1);
    }

    public String getUsername(){
        return prefs.getString(KEY_USERNAME, null);
    }


}
