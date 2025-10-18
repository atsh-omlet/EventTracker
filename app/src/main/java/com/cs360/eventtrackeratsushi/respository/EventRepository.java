package com.cs360.eventtrackeratsushi.respository;
import android.content.Context;
import com.cs360.eventtrackeratsushi.database.DatabaseHelper;
import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.util.SessionManager;

import java.util.ArrayList;

public class EventRepository {
    private final DatabaseHelper dbHelper;
    private final SessionManager sessionManager;

    private static EventRepository instance;
    public static EventRepository getInstance(Context context){
        if (instance == null){
            instance = new EventRepository(context);
        }
        return instance;
    }

    private EventRepository(Context context){
        dbHelper = DatabaseHelper.getInstance(context);
        sessionManager = new SessionManager(context);

    }

    public String getUsername(){
        return sessionManager.getUsername();
    }


    /**
     *  add new event to databae
     * @param title event title
     * @param date event date
     * @return true if succesful, false if not
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
     * updates existing event
     * @param eventId
     * @param newTitle
     * @return true if successful, false if not
     */
    public boolean updateEvent(int eventId, String newTitle, String newDate) {
        return dbHelper.updateEvent(eventId, newTitle, newDate);
    }

    /**
     *  deletes event by id,
     * @param eventId
     * @return true if succesful, false if not
     */
    public boolean deleteEvent(int eventId){
        return dbHelper.deleteEvent(eventId);
    }

    /**
     * @return all events associated with a userId, ordered by ascending datae
     */
    public ArrayList<Event> getEventsForUser() {
        return dbHelper.getEventsForUser(sessionManager.getUserId());
    }

}
