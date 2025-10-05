package com.cs360.eventtrackeratsushi.viewmodel;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.respository.EventRepository;

import java.util.Objects;


public class EventDetailsViewModel extends AndroidViewModel{
    private final EventRepository repository;
    private final MutableLiveData<String> eventName = new MutableLiveData<>("");
    private final MutableLiveData<String> eventDate = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private int eventId = -1;

    public EventDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = EventRepository.getInstance(application);
    }

    public LiveData<String> getEventName(){
        return eventName;
    }
    public LiveData<String> getEventDate(){
        return eventDate;
    }
    public LiveData<Boolean>  getSaveSuccess(){
        return saveSuccess;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setEventName(String name){
        eventName.setValue(name);
    }
    public void setEventDate(String date){

        eventDate.setValue(date);
    }

    public void loadEvent(int eventId){
        Event event = repository.getEvent(eventId);
        if (event != null) {
            this.eventId = eventId;
            eventName.setValue(event.getTitle());
            eventDate.setValue(event.getDate());
        }
    }

    public void saveEvent(){
        if (Objects.requireNonNull(eventName.getValue()).isEmpty()||
                Objects.requireNonNull(eventDate.getValue()).isEmpty()){
            errorMessage.setValue("Event name and date cannot be empty.");
            return;
        }
        boolean result;
        if (eventId == -1){
            result = repository.createEvent(eventName.getValue(), eventDate.getValue());
        }
        else {
            result = repository.updateEvent(eventId, eventName.getValue(), eventDate.getValue());
        }
        saveSuccess.setValue(result);
    }




}
