package com.cs360.eventtrackeratsushi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class DateUtils {
    /**
     * Returns a formatted date string for display use
     */
    public String formatDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            Date parsedDate = inputFormat.parse(date);
            assert parsedDate != null;

            Calendar calendar = Calendar.getInstance();
            Calendar parsedCal = Calendar.getInstance();
            parsedCal.setTime(parsedDate);

            String pattern = (parsedCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) ?
                    "MMM dd 'at' h:mm a" : "MMM dd, yyyy 'at' h:mm a";

            SimpleDateFormat outputFormat = new SimpleDateFormat(pattern,
                    Locale.getDefault());
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            return date;
        }
    }

    public String formatTime(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date parsedDate = inputFormat.parse(date);
            assert parsedDate != null;

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return timeFormat.format(parsedDate);
        } catch (ParseException e) {
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
            return System.currentTimeMillis();
        }
    }

}
