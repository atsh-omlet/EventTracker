package com.atsushi.event_tracker.respository;
import android.content.Context;
import android.util.Log;

import com.atsushi.event_tracker.database.DatabaseHelper;
import com.atsushi.event_tracker.util.SecurityUtils;
import com.atsushi.event_tracker.manager.SessionManager;

/**
 * Repository for user operations
 */
public class UserRepository {
    private final String TAG = "UserRepository";

    // Singleton instances
    private final DatabaseHelper dbHelper;
    private static UserRepository instance;
    // Session manager
    private final SessionManager sessionManager;

    /**
     * Singleton getter
     * @param context context
     * @return instance
     */
    public static UserRepository getInstance(Context context){
        if (instance == null){
            instance = new UserRepository(context);
        }
        return instance;
    }


    /**
     * Constructor
     * @param context context
     */
    private UserRepository(Context context){
        dbHelper = DatabaseHelper.getInstance(context);
        sessionManager = SessionManager.getInstance(context);
    }

    /**
     * checks if username and password match
     * @param username username
     * @param password password
     * @return true if successful, false if not
     */
    public boolean checkUser(String username, String password){
        return checkUsername(username) &&
                SecurityUtils.checkPassword(password, dbHelper.getPasssword(username));
    }

    /**
     * checks if password matches
     * @param password password
     * @return true if successful, false if not
     */
    public boolean checkPassword(String password){
        return SecurityUtils.checkPassword(password, dbHelper.getPasssword(getUsername()));
    }

    /**
     * checks if username exists
     * @param username username
     * @return true if exists, false if not
     */
    public boolean checkUsername(String username){
        return dbHelper.checkUsernameExists(username);
    }

    /**
     * gets user id
     * @return user id
     */
    public int getUserId(){
        return sessionManager.getUserId();
    }

    /**
     * gets username
     * @return username
     */
    public String getUsername(){
        return sessionManager.getUsername();
    }

    /**
     * checks if user is logged in
     * @return true if logged in, false if not
     */
    public boolean isLoggedIn(){
        return sessionManager.isLoggedIn();
    }

    /**
     * logs user in
     * @param username username
     * @param password password
     * @return true if successful, false if not
     */
    public boolean login(String username, String password){
        if (checkUser(username, password)){
            int userId = dbHelper.getUserId(username);
            sessionManager.saveLoginSession(userId, username);
            return true;
        }
        return false;
    }

    /**
     * logs user out
     */
    public void logout(){
        sessionManager.clearSession();
    }

    /**
     * creates new user
     * @param username username
     * @param password password
     * @param passwordConfirm password confirmation
     * @return true if successful, false if not
     */
    public boolean createUser(String username, String password, String passwordConfirm){
        if (password.equals(passwordConfirm)){
            String hashedPassword = SecurityUtils.hashPassword(password);
            dbHelper.createUser(username, hashedPassword);
            int userId = dbHelper.getUserId(username);
            sessionManager.saveLoginSession(userId, username);
            return true;
        }
        return false;
    }

    /**
     * updates password
     * @param newPassword new password
     * @return true if successful, false if not
     */
    public boolean updatePassword(String newPassword){
        String hashedPassword = SecurityUtils.hashPassword(newPassword);
        return dbHelper.updatePassword(getUserId(), hashedPassword);
    }

    /**
     * deletes user
     * @return true if successful, false if not
     */
    public boolean deleteUser(){
        boolean deleted = dbHelper.deleteUser(getUserId());
        //sessionManager.clearSession();
        Log.d(TAG, "isLoggedIn: " + sessionManager.isLoggedIn());
        return deleted;
    }




}
