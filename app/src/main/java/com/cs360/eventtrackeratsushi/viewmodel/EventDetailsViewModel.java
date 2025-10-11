package com.cs360.eventtrackeratsushi.viewmodel;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.respository.EventRepository;
import com.cs360.eventtrackeratsushi.util.DateUtils;
import com.cs360.eventtrackeratsushi.util.NotificationHelper;

import java.util.Date;
import java.util.Objects;


public class EventDetailsViewModel extends AndroidViewModel{
    private final String TAG = "EventDetailsViewModel";
    private final EventRepository repository;
    private final MutableLiveData<String> eventName = new MutableLiveData<>("");
    private final MutableLiveData<String> eventDate = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final DateUtils dateUtils = new DateUtils();

    private int eventId = -1;
    private final int THIRTY_MINUTES = 60000 * 30;

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
        Event event;
        if (eventId == -1){
            result = repository.createEvent(eventName.getValue(), eventDate.getValue());
            if (result){
                int newId = repository.getLastEventId();
                event = repository.getEvent(newId);
            } else {
                event = null;
            }
        }
        else {
            result = repository.updateEvent(eventId, eventName.getValue(), eventDate.getValue());
            event = repository.getEvent(eventId);
        }

        if (result && event != null){
            long eventTime = dateUtils.parseDateToMillis(event.getDate());
            long currentTime = System.currentTimeMillis();
            Log.d(TAG, "Event time: " + new Date(eventTime));
            Log.d(TAG, "Current time: " + new Date(currentTime));


            if (eventTime > currentTime){
                if (eventTime - currentTime > THIRTY_MINUTES) {
                    eventTime -= THIRTY_MINUTES;
                    NotificationHelper.scheduleNotification(getApplication(), event, eventTime);
                    Log.d(TAG, "Notification scheduled for event: " + event.getTitle() + " at " + new Date(eventTime));
                }
                else {
                    NotificationHelper.scheduleNotification(getApplication(), event, currentTime);
                    Log.d(TAG, "Notification scheduled for event: " + event.getTitle() + " at  + " + new Date(currentTime));
                }
            }
        }
        saveSuccess.setValue(result);
    }
    


}
