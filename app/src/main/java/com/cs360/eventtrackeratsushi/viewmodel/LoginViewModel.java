package com.cs360.eventtrackeratsushi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.cs360.eventtrackeratsushi.respository.UserRepository;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application){
        super(application);
        repository = UserRepository.getInstance(application);
        isLoggedIn.setValue(repository.isLoggedIn());
    }

    public LiveData<Boolean> getLoginStatus(){
        return isLoggedIn;
    }
    public LiveData<Boolean> getLoginSuccess() {return loginSuccess;}

    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }

    public void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Please enter both a username and password.");
            return;
        }
        if (repository.login(username, password)) {
            loginSuccess.setValue(true);
            isLoggedIn.setValue(repository.isLoggedIn());
        }
        else {
            errorMessage.setValue("Invalid username or password. Please try again.");
        }
    }

    public void createUser(String username, String password, String passwordConfirm){
        if (username.isEmpty()||password.isEmpty()||passwordConfirm.isEmpty()){
            errorMessage.setValue("Please enter a username and fill both password fields.");
            return;
        }
        else if (repository.checkUsername(username)){
            errorMessage.setValue("Username taken, please login.");
            return;
        }

        if (repository.createUser(username, password, passwordConfirm)) {
            loginSuccess.setValue(true);
            isLoggedIn.setValue(repository.isLoggedIn());
        }
        else {
            errorMessage.setValue("Passwords do not match. Please try again.");
        }
    }

    public void logout(){
        repository.logout();
        isLoggedIn.setValue(false);
        loginSuccess.setValue(false);
     }

}
