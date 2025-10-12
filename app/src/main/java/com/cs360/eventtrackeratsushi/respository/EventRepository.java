package com.cs360.eventtrackeratsushi.respository;
import android.content.Context;
import com.cs360.eventtrackeratsushi.database.DatabaseHelper;
import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.util.SessionManager;

import java.util.ArrayList;

/**
 * Repository for event operations
 */
public class EventRepository {
    // Singleton instances
    private final DatabaseHelper dbHelper;
    private static EventRepository instance;

    // Session manager
    private final SessionManager sessionManager;

    /**
     * Singleton getter
     * @param context context
     * @return instance
     */
    public static EventRepository getInstance(Context context){
        if (instance == null){
            instance = new EventRepository(context);
        }
        return instance;
    }

    /**
     * Constructor
     * @param context context
     */
    private EventRepository(Context context){
        dbHelper = DatabaseHelper.getInstance(context);
        sessionManager = SessionManager.getInstance(context);
    }

    /**
     * returns username
     * @return  username
     */
    public String getUsername(){
        return sessionManager.getUsername();
    }


    /**
     *  add new event to database
     * @param title event title
     * @param date event date
     * @return true if successful, false if not
     */
    public boolean createEvent(String title, String date) {
        return dbHelper.createEvent(title, date, sessionManager.getUserId());
    }

    /**
     * @return returns event
     */
    public Event getEvent(int eventId) {
        return dbHelper.getEvent(eventId);
    }

    /**
     * @return last event id
     */
    public int getLastEventId(){
        return dbHelper.getLastEventId();
    }

    /**
     * updates existing event
     * @param eventId event id
     * @param newTitle new title
     * @return true if successful, false if not
     */
    public boolean updateEvent(int eventId, String newTitle, String newDate) {
        return dbHelper.updateEvent(eventId, newTitle, newDate);
    }

    /**
     *  deletes event by id,
     * @param eventId event id
     * @return true if successful, false if not
     */
    public boolean deleteEvent(int eventId){
        return dbHelper.deleteEvent(eventId);
    }

    public boolean deleteAllEvents(){
        return dbHelper.deleteAllEvents(sessionManager.getUserId());
    }

    /**
     * @return all events associated with a userId, ordered by ascending datae
     */
    public ArrayList<Event> getEventsForUser() {
        return dbHelper.getEventsForUser(sessionManager.getUserId());
    }


}
