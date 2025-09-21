package com.cs360.eventtrackeratsushi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cs360.eventtrackeratsushi.model.Event;

import java.util.ArrayList;

/**
 * Helper clas to manage database creation, version management, and CRUD operations
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 2;

    // User logins database
    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
        private static final String COL_PHONE = "phone_number";
    }

    // Event database
    private static final class EventTable {
        private static final String TABLE = "events";
        private static final String COL_ID = "_id";
        private static final String COL_EVENT_TITLE = "title";
        private static final String COL_EVENT_DATE = "date";
        private static final String COL_USER_ID = "user_id";
    }

    /**
     *  Constructor
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *  called when database  is first created
     * @param db
     */
    @Override
     public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "create table " + UserTable.TABLE + " (" +
                UserTable.COL_ID + " integer primary key autoincrement, " +
                UserTable.COL_USERNAME + " text unique, " + UserTable.COL_PASSWORD +
                " text)";
        String CREATE_EVENT_TABLE = "create table " + EventTable.TABLE + " (" +
                EventTable.COL_ID + " integer primary key autoincrement, " +
                EventTable.COL_EVENT_TITLE + " text, " + EventTable.COL_EVENT_DATE + " text, "
                + EventTable.COL_USER_ID  + " integer, foreign key(" + EventTable.COL_USER_ID +
                ") references " + UserTable.TABLE + "(" +
                UserTable.COL_ID + "))";
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_EVENT_TABLE);
    }

    /**
     *  called whn database is upgraded
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + UserTable.TABLE);
        db.execSQL("drop table if exists " + EventTable.TABLE);
        onCreate(db);
    }

    /* User methods */
    /**
     *  Checks if a username already exists to maintain uniqueness
     * @param username
     * @return true if user name exists, false if not
     */
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + UserTable.TABLE + " where " + UserTable.COL_USERNAME +
                " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     *  Check if a username password pair exists
     * @param username
     * @param password
     * @return true if credentials are valid, false if not
     */
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from " + UserTable.TABLE + " where " + UserTable.COL_USERNAME +
                " = ? and " + UserTable.COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    /**
     * Adds new user to the user table
     * @param username
     * @param password
     * @return true if successful, false if not
     */
    public boolean createUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, username);
        values.put(UserTable.COL_PASSWORD, password);
        long result = db.insert(UserTable.TABLE, null, values);
        db.close();
        return result != -1;
    }


    /**
     * returns user id by username
     * @param username
     * @return usrId if found, -1 if not
     */
    public int getUserId(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select " + UserTable.COL_ID + " from " + UserTable.TABLE + " where " +
                UserTable.COL_USERNAME + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        int userId = -1;
        if (cursor.moveToFirst()){
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    /* Event methods */

    /**
     *  add new event to databae
     * @param title event title
     * @param date event date
     * @param userId userid
     * @return true if succesful, false if not
     */
    public boolean createEvent(String title, String date, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventTable.COL_EVENT_TITLE, title);
        values.put(EventTable.COL_EVENT_DATE, date);
        values.put(EventTable.COL_USER_ID, userId);
        long result = db.insert(EventTable.TABLE, null, values);
        db.close();
        return result != -1;
    }

    /**
     *
     * @param id
     * @return returns event by id
     */
    public Event getEvent(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = ("select * from " + EventTable.TABLE + " where " + EventTable.COL_ID
                + " = ?");
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            Event event = new Event (
                    cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EventTable.COL_EVENT_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EventTable.COL_EVENT_DATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.COL_USER_ID))
            );
            cursor.close();
            return event;
        }
        return null;
    }

    /**
     * updates existing event
     * @param eventId
     * @param newTitle
     * @param newDate
     * @return true if successful, false if not
     */
    public boolean updateEvent(int eventId, String newTitle, String newDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EventTable.COL_EVENT_TITLE, newTitle);
        values.put(EventTable.COL_EVENT_DATE, newDate);
        int rowsAffected = db.update(EventTable.TABLE, values, EventTable.COL_ID + "=?",
                new String[]{String.valueOf(eventId)});
        db.close();
        return rowsAffected > 0;
    }

    /**
     *  deletes event by id,
     * @param eventId
     * @return true if succesful, false if not
     */
    public boolean deleteEvent(int eventId){
            SQLiteDatabase db = this.getWritableDatabase();
            int rowsDeleted = db.delete(EventTable.TABLE, EventTable.COL_ID + "=?",
                    new String[]{String.valueOf(eventId)});
            db.close();
            return rowsDeleted > 0;
    }

    /**
     * @param userId
     * @return all events associated with a userId, ordered by ascending datae
     */
    public ArrayList<Event> getEventsForUser(int userId) {
        ArrayList<Event> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = ("select * from " + EventTable.TABLE + " where " + EventTable.COL_USER_ID
        + " = ? order by date(" + EventTable.COL_EVENT_DATE + ") ASC");
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)})){
            if (cursor.moveToFirst()) {
                do {
                    Event event = new Event(
                            cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.COL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(EventTable.COL_EVENT_TITLE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(EventTable.COL_EVENT_DATE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(EventTable.COL_USER_ID))
                    );

                    Log.d("DB_DEBUG", "ID: " + event.getId() +
                            ", Title: " + event.getTitle() +
                            ", Date: " + event.getDate() +
                            ", UserID: " + event.getUserId());

                    events.add(event);
                } while (cursor.moveToNext());
            }
        };
        return events;
    }
}