package com.cs360.eventtrackeratsushi.viewmodel;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.respository.EventRepository;
import com.cs360.eventtrackeratsushi.util.AppStateHelper;
import com.cs360.eventtrackeratsushi.util.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for DashboardActivity
 */
public class DashboardViewModel extends AndroidViewModel{
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

    public boolean shouldRequestInitialPermissions() {
        if (!appStateHelper.isPermissionsChecked()) {
            appStateHelper.setPermissionsChecked();
            return true;
        }
        return false;
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

    /**
     * Deletes all events from the database
     */
    public void deleteAllEvents(){
        Event[] events = new Event[repository.getEventsForUser().size()];
        events = repository.getEventsForUser().toArray(events);
        for (Event event : events){
            NotificationHelper.cancelNotification(getApplication(), event.getId());
        }
        repository.deleteAllEvents();
        loadEvents();
    }

    /**
     * Gets the username for the current user
     * @return  The username for the current user
     */
    public LiveData<String> getUsername(){
        return username;
    }

    /**
     * Sets the username for the current user
     */
    public void clearEvents(){
        events.setValue(new ArrayList<>());
    }

}
