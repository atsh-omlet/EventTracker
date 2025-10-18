package com.atsushi.event_tracker.viewmodel;

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

import com.atsushi.event_tracker.model.Event;
import com.atsushi.event_tracker.respository.EventRepository;
import com.atsushi.event_tracker.util.AppStateHelper;
import com.atsushi.event_tracker.util.DateUtils;
import com.atsushi.event_tracker.util.NotificationHelper;

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
    private final MutableLiveData<Boolean> shouldReschedule = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final AppStateHelper appStateHelper = AppStateHelper.getInstance(getApplication());
    private final AlarmManager alarmManager;

    /**
     * Constructor for DashboardViewModel
     * @param application  The application
     */
    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = EventRepository.getInstance(application);
        alarmManager = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
        username.postValue(repository.getUsername());
        loadEvents();
    }

    @Override
    protected void onCleared(){
        super.onCleared();
        executor.shutdown();
    }

    /**
     * Exposes the events for the current user
     * @return  The events for the current user
     */
    public LiveData<List<Event>> getEvents(){
        return events;
    }

    /**
     * Exposes flag for if the dialog for granting exact alarms should be shown
     * @return  The shouldShowExactAlarmGrantDialog flag
     */
    public LiveData<Boolean> getShouldShowExactAlarmGrantDialog(){
        return shouldShowExactAlarmGrantDialog;
    }

    /**
     * Sets if the dialog for granting exact alarms should be shown
     * @param show true if the dialog should be shown, false otherwise
     */
    public void setShouldShowExactAlarmGrantDialog(boolean show){
        if (Looper.myLooper()==Looper.getMainLooper()){
            shouldShowExactAlarmGrantDialog.setValue(show);
        } else {
            shouldShowExactAlarmGrantDialog.postValue(show);
        }
    }



    /**
     * Exposes flag for if notifications should be rescheduled
     * @return  The shouldReschedule flag
     */
    public LiveData<Boolean> getShouldReschedule(){
        return shouldReschedule;
    }

    public void setShouldReschedule(boolean reschedule){
        if (Looper.myLooper()==Looper.getMainLooper()) {
            shouldReschedule.setValue(reschedule);
        } else {
            shouldReschedule.postValue(reschedule);
        }
    }

    private Boolean couldSchedule = null;


    /**
     * Sets reschedule flag if exact alarm permission has been granted when previously denied
     */
    public void rescheduleTrigger(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean canNowSchedule = alarmManager.canScheduleExactAlarms();

            if (couldSchedule == null) {
                couldSchedule = canNowSchedule;
                return;
            }

            if (!couldSchedule && canNowSchedule) {
                Log.d(TAG, "rescheduleTrigger: Exact alarm granted, triggering reschedule");
                setShouldReschedule(true);
                appStateHelper.resetOptOutReminder();
            } else if (couldSchedule && !canNowSchedule){
                Log.d(TAG, "rescheduleTrigger: Exact alarm permission revoked");
                setShouldReschedule(true);
            }

            couldSchedule = canNowSchedule;
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
     * Returns if permissions were granted previously
     * @return  true if permissions were granted previously, false otherwise
     */
    public boolean wasPermissionGranted(){
        //Log.d(TAG, "wasPermissionGranted: " + appStateHelper.wasPermissionGranted());
        return appStateHelper.wasPermissionGranted();
    }

    /**
     * Sets permissions granted to be true if wasPermissionGranted is false
     * and alarm manager can schedule exact alarms
     */
    public void autoUpdatePermissionGranted(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager != null && alarmManager.canScheduleExactAlarms()
                && !wasPermissionGranted()) {
                setPermissionGranted(true);
                Log.d(TAG, "autoUpdatePermissionGranted: true");
            }
        }
    }

    /**
     * Sets if permissions were granted previously
     * @param granted  true if permissions were granted previously, false otherwise
     */
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

    /**
     * Reschedules the notifications for all events in the future
     */
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
            setShouldReschedule(false);
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
        Log.d(TAG, "checkExactAlarmPermission: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager == null) {
                Log.d(TAG, "    checkExactAlarmPermission: alarmManager is null");
                return;
            }
            if (!wasPermissionGranted()){
                Log.d(TAG, "    checkExactAlarmPermission: wasPermissionGranted is false");
                return;
            }
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d(TAG, "    checkExactAlarmPermission: Exact alarm permission denied");
                Log.d(TAG, "    shouldOptOutReminder: " + shouldOptOutReminder());
                setShouldShowExactAlarmGrantDialog(true);
                return;
            }
            Log.d(TAG, "    checkExactAlarmPermission: Exact alarm permission granted");
        }
    }



}


