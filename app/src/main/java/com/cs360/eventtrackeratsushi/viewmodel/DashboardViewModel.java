package com.cs360.eventtrackeratsushi.viewmodel;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.respository.EventRepository;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel{
    private final EventRepository repository;
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new EventRepository(application);
        username.setValue(repository.getUsername());
        loadEvents();
    }

    public LiveData<List<Event>> getEvents(){
        return events;
    }

    public void loadEvents(){
        events.setValue(repository.getEventsForUser());
    }

    public void deleteEvent(Event event){
        repository.deleteEvent(event.getId());
        loadEvents();
    }

    public LiveData<String> getUsername(){
        return username;
    }

    public void createEvent(String title, String date){
        repository.createEvent(title, date);
        loadEvents();
    }

    public void updateEvent(int eventId, String newTitle, String newDate){
        repository.updateEvent(eventId, newTitle, newDate);
        loadEvents();
    }
}
