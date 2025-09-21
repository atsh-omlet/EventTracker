package com.cs360.eventtrackeratsushi.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Model class representing events
 */
public class Event {
    private final int id;
    private String title;
    private String date;
    private final int userId;

    /**
     *  Constructor
     * @param id
     * @param title
     * @param date
     * @param userId
     */
    public Event( int id, String title, String date, int userId){
        this.id = id;
        this.title = title;
        this.date = date;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getDate(){
        return date;
    }

    public int getUserId(){
        return userId;
    }

    /**
     * Returns a formatted date string for display use
     */
    public String getFormattedDate() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd 'at' h:mm a",
                    Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            return date;
        }
    }



}
