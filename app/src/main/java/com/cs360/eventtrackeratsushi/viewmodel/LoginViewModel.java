package com.cs360.eventtrackeratsushi.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.respository.EventRepository;
import com.cs360.eventtrackeratsushi.respository.UserRepository;
import com.cs360.eventtrackeratsushi.util.NotificationHelper;

public class LoginViewModel extends AndroidViewModel {
    private final String TAG = "LoginViewModel";
    private final UserRepository repository;
    private final EventRepository eventRepository;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> passwordCheck = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application){
        super(application);
        repository = UserRepository.getInstance(application);
        eventRepository = EventRepository.getInstance(application);
        isLoggedIn.setValue(repository.isLoggedIn());
    }

    public LiveData<Boolean> getLoginStatus(){
        return isLoggedIn;
    }
    public LiveData<Boolean> getLoginSuccess() {return loginSuccess;}

    public LiveData<String> getMessage(){
        return message;
    }
    public LiveData<Boolean> getPasswordCheck(){ return passwordCheck;}

    /**
     * Logs in the user
     * @param username  The user's username
     * @param password  The user's password
     */
    public void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            message.setValue("Please enter both a username and password.");
            return;
        }
        if (repository.login(username, password)) {
            loginSuccess.setValue(true);
            isLoggedIn.setValue(repository.isLoggedIn());
            message.setValue("Welcome back, " + username + ".");
            Log.d(TAG, "login: " + repository.isLoggedIn());
        }
        else {
            message.setValue("Invalid username or password. Please try again.");
        }
    }

    /**
     * Creates a new user
     * @param username  The user's username
     * @param password  The user's password
     * @param passwordConfirm  The user's password confirmation
     */
    public void createUser(String username, String password, String passwordConfirm){
        if (username.isEmpty()||password.isEmpty()||passwordConfirm.isEmpty()){
            message.setValue("Please enter a username and fill both password fields.");
            return;
        }
        else if (repository.checkUsername(username)){
            message.setValue("Username taken, please login.");
            return;
        }

        if (repository.createUser(username, password, passwordConfirm)) {
            loginSuccess.setValue(true);
            message.setValue("Account created. Welcome, " + username + ".");
            isLoggedIn.setValue(repository.isLoggedIn());
        }
        else {
            message.setValue("Passwords do not match. Please try again.");
        }
    }

    /**
     * Checks the user's password
     * @param password  The user's password
     */
    public void checkPassword(String password){
        if (password.isEmpty()){
            message.setValue("Please enter a password.");
            return;
        }
        if (repository.checkPassword(password)){
            passwordCheck.setValue(true);
        }
        else {
            message.setValue("Invalid password. Please try again.");
        }
    }

    /**
     * Updates the user's password
     * @param newPassword  The user's new password
     * @param confirmPassword  The user's password confirmation
     */
    public void updatePassword(String newPassword, String confirmPassword){
        if (newPassword.isEmpty() || confirmPassword.isEmpty()){
            message.setValue("Please enter a new password and confirm it.");
        } else if (!newPassword.equals(confirmPassword)){
            message.setValue("Passwords do not match. Please try again.");
        }
        try {
            if (repository.updatePassword(newPassword)){
                message.setValue("Password updated successfully.");
                passwordCheck.setValue(true);
            }
        } catch (Exception e){
            message.setValue("Error updating password. Please try again.");
            Log.d(TAG, "updatePassword: " + e.getMessage());
        }

    }

    /**
     * Deletes the user's account
     * @param password  The user's password
     */
    public void deleteAccount(String password){
        if (password.isEmpty()){
            message.setValue("Please enter a password.");
            return;
        }else if (!repository.checkPassword(password)) {
            message.setValue("Invalid password. Please try again.");
            return;
        }
        try {
            Event[] events = new Event[eventRepository.getEventsForUser().size()];
            events = eventRepository.getEventsForUser().toArray(events);
            for (Event event : events){
                NotificationHelper.cancelNotification(getApplication(), event.getId());
            }
            if (repository.deleteUser()){
                repository.logout();
                message.setValue("Account deleted successfully.");
                passwordCheck.setValue(true);
            }
        } catch (Exception e){
            message.setValue("Error deleting account. Please try again.");
            Log.d(TAG, "deleteAccount: " + e.getMessage());
        }
    }

    /**
     * Logs out the user
     */
    public void logout(){
        repository.logout();
        isLoggedIn.setValue(false);
        loginSuccess.setValue(false);
        passwordCheck.setValue(false);
     }

}
