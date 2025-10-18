package com.atsushi.event_tracker.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class DateUtils {
    private static final String TAG = "DateUtils";
    /**
     * Returns a formatted time string
     * @param date  The date to format
     * @return  The formatted time string
     */
    public String formatTime(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            assert parsedDate != null;

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return timeFormat.format(parsedDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return date;
        }
    }

    /**
     * Returns a formatted date string
     * @param date  The date to format
     * @return  The formatted date string
     */
    public String formatDate(String date){
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            assert parsedDate != null;

            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return timeFormat.format(parsedDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return date;
        }
    }

    /**
     * Returns a formatted date string
     * @param date  The date to format
     * @return  The formatted date string
     */
    public String formatWeekday(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            Date parsedDate = inputFormat.parse(date);
            assert parsedDate != null;

            Calendar calendar = Calendar.getInstance();
            Calendar parsedCal = Calendar.getInstance();
            parsedCal.setTime(parsedDate);

            String pattern = (parsedCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) ?
                    "EEEE, MMM dd" : "EEEE, MMM dd, yyyy";

            SimpleDateFormat outputFormat = new SimpleDateFormat(pattern,
                    Locale.getDefault());
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
            return date;
        }
    }

    public long parseDateToMillis(String dateString) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            java.util.Date date = sdf.parse(dateString);
            assert date != null;
            return date.getTime();
        } catch (Exception e) {
            Log.e("TAG", "Error parsing date: " + e.getMessage());
            return System.currentTimeMillis();
        }
    }

    public boolean isToday(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            Date parsedDate = inputFormat.parse(date);
            assert parsedDate != null;

            Calendar calendar = Calendar.getInstance();
            Calendar parsedCal = Calendar.getInstance();
            parsedCal.setTime(parsedDate);

            return parsedCal.get(Calendar.DATE) == calendar.get(Calendar.DATE) &&
                    parsedCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    parsedCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
        } catch (Exception e) {
            Log.e("TAG", "Error parsing date: " + e.getMessage());
            return false;
        }
    }

}
