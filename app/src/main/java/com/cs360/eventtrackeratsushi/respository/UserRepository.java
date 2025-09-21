package com.cs360.eventtrackeratsushi.respository;
import android.content.Context;
import com.cs360.eventtrackeratsushi.database.DatabaseHelper;
import com.cs360.eventtrackeratsushi.util.SecurityUtils;
import com.cs360.eventtrackeratsushi.util.SessionManager;

public class UserRepository {
    private final DatabaseHelper dbHelper;
    private final SessionManager sessionManager;


    public UserRepository(Context context){
        dbHelper = new DatabaseHelper(context);
        sessionManager = new SessionManager(context);
    }

    public boolean checkUser(String username, String password){
        return dbHelper.checkUser(username, password);
    }

    public boolean checkUsername(String username){
        return dbHelper.checkUsernameExists(username);
    }

    public int getUserId(){
        return sessionManager.getUserId();
    }

    public String getUsername(){
        return sessionManager.getUsername();
    }

    public boolean isLoggedIn(){
        return sessionManager.isLoggedIn();
    }

    public boolean login(String username, String password){
        String hashedPassword = SecurityUtils.hashPassword(password);
        if (checkUser(username, hashedPassword)){
            int userId = dbHelper.getUserId(username);
            sessionManager.saveLoginSession(userId, username);
            return true;
        }
        return false;
    }

    public void logout(){
        sessionManager.clearSession();
    }

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




}
