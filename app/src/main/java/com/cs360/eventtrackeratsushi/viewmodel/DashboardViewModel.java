package com.cs360.eventtrackeratsushi.viewmodel;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.respository.EventRepository;
import com.cs360.eventtrackeratsushi.util.AppStateHelper;
import com.cs360.eventtrackeratsushi.util.DateUtils;
import com.cs360.eventtrackeratsushi.util.NotificationHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for DashboardActivity
 */
public class DashboardViewModel extends AndroidViewModel{
    private final String TAG = "DashboardViewModel";
    private final int THIRTY_MINUTES = 30 * 60 * 1000;
    private final EventRepository repository;
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();

    private final AppStateHelper appStateHelper;

    /**
     * Constructor for DashboardViewModel
     * @param application  The application
     */
    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = EventRepository.getInstance(application);
        appStateHelper = AppStateHelper.getInstance(application);
        username.setValue(repository.getUsername());
        loadEvents();
    }

    /**
     * Gets the events for the current user
     * @return  The events for the current user
     */
    public LiveData<List<Event>> getEvents(){
        return events;
    }


    /**
     * Gets if initial permission request was made
     * @return  true if initial permission request was made, false otherwise
     */
    public boolean shouldRequestInitialPermissions() {
        if (!appStateHelper.isPermissionsChecked()) {
            appStateHelper.setPermissionsChecked();
            return true;
        }
        return false;
    }

    /**
     * Gets if user wishes to opt out of granting permissions
     * @return  true if user wishes to opt out, false otherwise
     */
    public boolean shouldOptOutReminder(){
        return appStateHelper.isOptOutReminder();
    }

    /**
     * Set opt out to true;
     */
    public void setOptOutReminder(){
        appStateHelper.setOptOutReminder();
    }

    /**
     * Returns if notifications should be rescheduled
     * @return
     */
    public boolean shouldRescheduleNotifications(){
        return appStateHelper.shouldRescheduleNotifications();
    }

    /**
     * Sets if notifications should be rescheduled
     * @param reschedule  true if notifications should be rescheduled, false otherwise
     */
    public void setRescheduleNotifications(boolean reschedule){
        appStateHelper.setRescheduleNotifications(reschedule);
    }

    public boolean wasPermissionGranted(){
        return appStateHelper.wasPermissionGranted();
    }

    public void setPermissionGranted(boolean granted){
        appStateHelper.setPermissionGranted(granted);
    }

    /**
     * Loads the events for the current user from the database
     */
    public void loadEvents(){
        events.setValue(repository.getEventsForUser());
    }

    /**
     * Deletes the event from the database
     * @param event  The event to delete
     */
    public void deleteEvent(Event event){
        repository.deleteEvent(event.getId());
        NotificationHelper.cancelNotification(getApplication(), event.getId());
        loadEvents();
    }

    public void rescheduleNotifications(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            EventRepository repository = EventRepository.getInstance(getApplication());
            List<Event> allEvents = repository.getEventsForUser();
            DateUtils dateUtils = new DateUtils();

            long currentTime = System.currentTimeMillis();
            Log.d(TAG, "Current time: " + currentTime);

            for (Event event : allEvents) {
                long eventTimeMillis = dateUtils.parseDateToMillis(event.getDate());

                // Only re-schedule notifications for events that are in the future
                if (eventTimeMillis > currentTime) {
                    long notificationTime = eventTimeMillis - THIRTY_MINUTES;


                    if (notificationTime <= currentTime) {
                        notificationTime = currentTime + 5000;
                    }

                    try {
                        NotificationHelper.scheduleNotification(getApplication(), event, notificationTime);
                        Log.d(TAG, "Re-scheduled notification for event: " + event.getTitle());
                    } catch (SecurityException e) {
                        Log.e(TAG, "Failed to re-schedule notification for " + event.getTitle(), e);
                    }
                }
            }
        });

    }


    /**
     * Gets the username for the current user
     * @return  The username for the current user
     */
    public LiveData<String> getUsername(){
        return username;
    }


}


