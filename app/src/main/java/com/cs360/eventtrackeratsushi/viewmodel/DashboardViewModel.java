package com.cs360.eventtrackeratsushi.viewmodel;

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
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

import java.util.Date;
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
    private final MutableLiveData<Boolean> shouldShowExactAlarmGrantDialog = new MutableLiveData<>();
    private final Application application;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final AppStateHelper appStateHelper = AppStateHelper.getInstance(getApplication());

    /**
     * Constructor for DashboardViewModel
     * @param application  The application
     */
    public DashboardViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        repository = EventRepository.getInstance(application);
        username.postValue(repository.getUsername());
        loadEvents();
    }

    @Override
    protected void onCleared(){
        super.onCleared();
        executor.shutdown();
    }

    /**
     * Gets the events for the current user
     * @return  The events for the current user
     */
    public LiveData<List<Event>> getEvents(){
        return events;
    }

    public LiveData<Boolean> getShouldShowExactAlarmGrantDialog(){
        return shouldShowExactAlarmGrantDialog;
    }

    public void setShouldShowExactAlarmGrantDialog(boolean show){
        if (Looper.myLooper()==Looper.getMainLooper()){
            shouldShowExactAlarmGrantDialog.setValue(show);
        } else {
            shouldShowExactAlarmGrantDialog.postValue(show);
        }
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
        if (Looper.myLooper() == Looper.getMainLooper()) {
            events.setValue(repository.getEventsForUser());
        } else {
            events.postValue(repository.getEventsForUser());
        }
    }

    /**
     * Deletes the event from the database
     * @param event  The event to delete
     */
    public void deleteEvent(Event event){
        executor.execute(() -> {
            int eventId = event.getId();
            NotificationHelper.cancelNotification(getApplication(), eventId);
            repository.deleteEvent(eventId);
            appStateHelper.setEventsModified(true);
            Log.d(TAG, "Event deleted, id: " + eventId);
            loadEvents();
        });
    }

    public void rescheduleNotifications(){
        executor.execute(() -> {
            EventRepository repository = EventRepository.getInstance(getApplication());
            List<Event> allEvents = repository.getEventsForUser();
            DateUtils dateUtils = new DateUtils();

            long currentTime = System.currentTimeMillis();
            Log.d(TAG, "Rescheduling notifications for all events...");
            Log.d(TAG, "Current time: " + new Date(currentTime));

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
                        Log.d(TAG, "    Re-scheduled notification for event: " + event.getTitle()
                            + " at " + new Date(notificationTime));
                    } catch (SecurityException e) {
                        Log.e(TAG, "    Failed to re-schedule notification for " + event.getTitle(), e);
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

    public void checkExactAlarmPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);
            // only true when the app is not running for the first time

            if (alarmManager != null && wasPermissionGranted()
                    && !alarmManager.canScheduleExactAlarms()) {
                Log.d(TAG, "shouldOptOutReminder: " + shouldOptOutReminder());
                setShouldShowExactAlarmGrantDialog(true);

            }
            if (alarmManager != null && shouldRescheduleNotifications()
                    && alarmManager.canScheduleExactAlarms()) {
                rescheduleNotifications();
                setRescheduleNotifications(false);
            }
        }
    }



}


