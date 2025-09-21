package com.cs360.eventtrackeratsushi.util;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final String PREF_NAME =  "LoginPrefs";
    private final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private final String KEY_USERNAME = "username";
    private final String KEY_USERID = "userId";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
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
