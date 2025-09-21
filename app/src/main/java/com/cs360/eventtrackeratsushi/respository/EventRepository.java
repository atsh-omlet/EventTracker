package com.cs360.eventtrackeratsushi.respository;
import android.content.Context;
import com.cs360.eventtrackeratsushi.database.DatabaseHelper;
import com.cs360.eventtrackeratsushi.model.Event;

import java.util.ArrayList;

public class EventRepository {
    private final DatabaseHelper dbHelper;

    public EventRepository(Context context){
        dbHelper = new DatabaseHelper(context);
    }

    /**
     *  add new event to databae
     * @param title event title
     * @param date event date
     * @param userId userid
     * @return true if succesful, false if not
     */
    public boolean createEvent(String title, String date, int userId) {
        return dbHelper.createEvent(title, date, userId);
    }

    /**
     *
     * @param id
     * @return returns event by id
     */
    public Event getEvent(int id) {
        return dbHelper.getEvent(id);
    }

    /**
     * updates existing event
     * @param eventId
     * @param newTitle
     * @param newDate
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
     * @param userId
     * @return all events associated with a userId, ordered by ascending datae
     */
    public ArrayList<Event> getEventsForUser(int userId) {
        return dbHelper.getEventsForUser(userId);
    }

}
