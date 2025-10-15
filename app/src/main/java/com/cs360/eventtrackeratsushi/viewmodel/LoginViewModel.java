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

/**
 * ViewModel for LoginActivity and handles account related functions
 */
public class LoginViewModel extends AndroidViewModel {
    private final String TAG = "LoginViewModel";
    private final UserRepository repository;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    /**
     * Constructor for LoginViewModel
     * @param application  The application
     */
    public LoginViewModel(@NonNull Application application){
        super(application);
        repository = UserRepository.getInstance(application);
        isLoggedIn.setValue(repository.isLoggedIn());
    }

    /**
     * Gets the login status
     * @return
     */
    public LiveData<Boolean> getLoginStatus(){
        return isLoggedIn;
    }

    /**
     * Gets the login success
     * @return  The login success
     */
    public LiveData<Boolean> getLoginSuccess() {return loginSuccess;}

    /**
     * Gets the message
     * @return  The message
     */
    public LiveData<String> getMessage(){
        return message;
    }



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


}
