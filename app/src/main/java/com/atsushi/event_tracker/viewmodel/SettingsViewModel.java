package com.atsushi.event_tracker.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.atsushi.event_tracker.model.Event;
import com.atsushi.event_tracker.respository.EventRepository;
import com.atsushi.event_tracker.respository.UserRepository;
import com.atsushi.event_tracker.manager.AppStateHelper;
import com.atsushi.event_tracker.notification.NotificationHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Consolidated ViewModel for SettingsActivity to handle cross-cutting account/event logic.
 */
public class SettingsViewModel extends AndroidViewModel {

    private static final String TAG = "SettingsViewModel";
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> passwordCheck = new MutableLiveData<>();
    private final AppStateHelper appStateHelper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = UserRepository.getInstance(application);
        this.eventRepository = EventRepository.getInstance(application);
        this.appStateHelper = AppStateHelper.getInstance(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }

    /**
     * Gets the message LiveData
     * @return  The message LiveData
     */
    public LiveData<String> getMessage() {
        return message;
    }


    /**
     * Gets the passwordCheck LiveData
     * @return  The passwordCheck LiveData
     */
    public LiveData<Boolean> getPasswordCheck() {
        return passwordCheck;
    }

    /**
     * Clears credentials and events.
     */
    public void logout() {
        String username = userRepository.getUsername();
        userRepository.logout();
        message.postValue("Logged out of " + username + ".");
    }

    /**
     * Deletes all events for the current user
     */
    public void deleteAllEvents() {

        Event[] events = eventRepository.getEventsForUser().toArray(new Event[0]);
        for (Event event : events){
            NotificationHelper.cancelNotification(getApplication(), event.getId());
        }
        executor.execute(() -> {
            eventRepository.deleteAllEvents();
            appStateHelper.setEventsModified(true);
            message.postValue("All events cleared.");
        });

    }

    /**
     * Deletes the user's account
     */
    public void deleteAccount(String password){
        if (password.isEmpty()){
            message.postValue("Please enter a password.");
            return;
        }else if (!userRepository.checkPassword(password)) {
            message.postValue("Invalid password. Please try again.");
            return;
        }
        try {
            Event[] events = eventRepository.getEventsForUser().toArray(new Event[0]);
            for (Event event : events){
                NotificationHelper.cancelNotification(getApplication(), event.getId());
            }
            executor.execute(() -> {
                if (userRepository.deleteUser()){
                    String username = userRepository.getUsername();
                    int userId = userRepository.getUserId();
                    userRepository.logout();
                    message.postValue("Account deleted.\nGoodbye, " + username + ".");
                    Log.d(TAG, "Account deleted. userId: "
                            + userId + ", username: " + username + ".");
                    passwordCheck.postValue(true);
                }
            });
        } catch (Exception e){
            message.postValue("Error deleting account. Please try again.");
            Log.e(TAG, "deleteAccount: " + e.getMessage());
        }
    }

    /**
     * Checks the user's password
     */
    public void checkPassword(String password){
        if (password.isEmpty()){
            message.postValue("Please enter a password.");
            return;
        }
        if (userRepository.checkPassword(password)){
            passwordCheck.postValue(true);
        }
        else {
            message.postValue("Invalid password. Please try again.");
        }
    }

    /**
     * Updates the user's password
     * @param newPassword  The user's new password
     * @param confirmPassword  The user's password confirmation
     */
    public void updatePassword(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            message.postValue("Please enter a new password and confirm it.");
            return;
        } else if (!newPassword.equals(confirmPassword)) {
            message.postValue("Passwords do not match. Please try again.");
            return;
        }
        try {
            executor.execute(() -> {
                if (userRepository.updatePassword(newPassword)) {
                    message.postValue("Password updated successfully.");
                    passwordCheck.postValue(true);
                }
            });
        } catch (Exception e) {
            message.postValue("Error updating password. Please try again.");
            Log.d(TAG, "updatePassword: " + e.getMessage());
        }
    }
}