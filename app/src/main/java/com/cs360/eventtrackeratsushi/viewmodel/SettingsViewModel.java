package com.cs360.eventtrackeratsushi.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.respository.EventRepository;
import com.cs360.eventtrackeratsushi.respository.UserRepository;
import com.cs360.eventtrackeratsushi.util.NotificationHelper;

/**
 * Consolidated ViewModel for SettingsActivity to handle cross-cutting account/event logic.
 */
public class SettingsViewModel extends AndroidViewModel {

    private static final String TAG = "SettingsViewModel";
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> passwordCheck = new MutableLiveData<>();

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = UserRepository.getInstance(application);
        this.eventRepository = EventRepository.getInstance(application);
    }

    /**
     * Gets the message LiveData
     * @return  The message LiveData
     */
    public MutableLiveData<String> getMessage() {
        return message;
    }

    /**
     * Gets the passwordCheck LiveData
     * @return  The passwordCheck LiveData
     */
    public MutableLiveData<Boolean> getPasswordCheck() {
        return passwordCheck;
    }

    /**
     * Clears credentials and events.
     */
    public void logout() {
        String username = userRepository.getUsername();
        userRepository.logout();
        message.setValue("Logged out of " + username + ".");
    }

    /**
     * Deletes all events for the current user
     */
    public void deleteAllEvents() {

        Event[] events = eventRepository.getEventsForUser().toArray(new Event[0]);
        for (Event event : events){
            NotificationHelper.cancelNotification(getApplication(), event.getId());
        }
        eventRepository.deleteAllEvents();

    }

    /**
     * Deletes the user's account
     */
    public void deleteAccount(String password){
        if (password.isEmpty()){
            message.setValue("Please enter a password.");
            return;
        }else if (!userRepository.checkPassword(password)) {
            message.setValue("Invalid password. Please try again.");
            return;
        }
        try {
            Event[] events = new Event[eventRepository.getEventsForUser().size()];
            events = eventRepository.getEventsForUser().toArray(events);
            for (Event event : events){
                NotificationHelper.cancelNotification(getApplication(), event.getId());
            }
            if (userRepository.deleteUser()){
                String username = userRepository.getUsername();
                userRepository.logout();
                message.setValue("Account deleted.\nGoodbye, " + username + ".");
                passwordCheck.setValue(true);
            }
        } catch (Exception e){
            message.setValue("Error deleting account. Please try again.");
            Log.d(TAG, "deleteAccount: " + e.getMessage());
        }
    }

    /**
     * Checks the user's password
     */
    public void checkPassword(String password){
        if (password.isEmpty()){
            message.setValue("Please enter a password.");
            return;
        }
        if (userRepository.checkPassword(password)){
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
    public void updatePassword(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            message.setValue("Please enter a new password and confirm it.");
            return;
        } else if (!newPassword.equals(confirmPassword)) {
            message.setValue("Passwords do not match. Please try again.");
            return;
        }
        try {
            if (userRepository.updatePassword(newPassword)) {
                message.setValue("Password updated successfully.");
                passwordCheck.setValue(true);
            }
        } catch (Exception e) {
            message.setValue("Error updating password. Please try again.");
            Log.d(TAG, "updatePassword: " + e.getMessage());
        }
    }
}