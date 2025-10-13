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

    /**
     * Constructor for EventDetailsViewModel
     * @param application  The application
     */
    public EventDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = EventRepository.getInstance(application);
    }

    /**
     * Gets the event name
     * @return  The event name
     */
    public LiveData<String> getEventName(){
        return eventName;
    }

    /**
     * Gets the event date
     * @return  The event date
     */
    public LiveData<String> getEventDate(){
        return eventDate;
    }

    /**
     * Gets the success of saving the event
     * @return  The success of saving the event
     */
    public LiveData<Boolean>  getSaveSuccess(){
        return saveSuccess;
    }

    /**
     * Gets the error message
     * @return  The error message
     */
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the event name
     * @param name  The event name
     */
    public void setEventName(String name){
        eventName.setValue(name);
    }

    /**
     * Sets the event date
     * @param date  The event date
     */
    public void setEventDate(String date){

        eventDate.setValue(date);
    }

    /**
     * Loads the event from the database
     */
    public void loadEvent(int eventId){
        Event event = repository.getEvent(eventId);
        if (event != null) {
            this.eventId = eventId;
            eventName.setValue(event.getTitle());
            eventDate.setValue(event.getDate());
        }
    }

    /**
     * Saves the event to the database
     */
    public void saveEvent(){

        if (Objects.requireNonNull(eventName.getValue()).isEmpty()||
                Objects.requireNonNull(eventDate.getValue()).isEmpty()){
            errorMessage.setValue("Event name and date cannot be empty.");
            return;
        }
        boolean result;

        Event event;
        if (eventId == -1){ // Create new event
            result = repository.createEvent(eventName.getValue(), eventDate.getValue());
            if (result){
                int newId = repository.getLastEventId();
                event = repository.getEvent(newId);
            } else {
                event = null;
            }
        }
        else { // Update existing event
            result = repository.updateEvent(eventId, eventName.getValue(), eventDate.getValue());
            event = repository.getEvent(eventId);
        }

        if (result && event != null){ // Schedule notification for event
            long eventTime = dateUtils.parseDateToMillis(event.getDate());
            long currentTime = System.currentTimeMillis();
            Log.d(TAG, "Event time: " + new Date(eventTime));
            Log.d(TAG, "Current time: " + new Date(currentTime));


            // If event time is in the future, schedule notification
            if (eventTime > currentTime){
                if (eventTime - currentTime > THIRTY_MINUTES) { // If event is more than 30 minutes away, schedule notification
                    eventTime -= THIRTY_MINUTES;
                    NotificationHelper.scheduleNotification(getApplication(), event, eventTime);
                    Log.d(TAG, "Notification scheduled for event: " + event.getTitle() + " at " + new Date(eventTime));
                }
                else { // If event is less than 30 minutes away, schedule notification now
                    NotificationHelper.scheduleNotification(getApplication(), event, currentTime);
                    Log.d(TAG, "Notification scheduled for event: " + event.getTitle() + " at  + " + new Date(currentTime));
                }
            }
        }
        saveSuccess.setValue(result);
    }
    


}
