package com.cs360.eventtrackeratsushi.util;

import android.content.Context;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.util.Calendar;
import java.util.Locale;

public class DateTimePickerHelper {

    public interface OnDateTimeSelectedListener {
        void onDateTimeSelected(String dateTime);
    }


    /**
     * Show date picker dialog to select event date. Past dates are restricted.
     */
    public static void showDateTimePicker(Context context, OnDateTimeSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    // show time picker to select time after date is selected
                    showTimePicker(context, year, month, dayOfMonth, listener);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        //datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); //prevent past dates
        datePicker.show();
    }

    /**
     * Showtime picker dialog to select event time
     * @param year year selected
     * @param month month selected
     * @param dayOfMonth day selected
     */
    private static void showTimePicker(Context context, int year, int month, int dayOfMonth,
                                OnDateTimeSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(
                context,
                (view, hourOfday, minute1)->{
                    String selectedDateTime = String.format(Locale.getDefault(),
                    "%04d-%02d-%02d %02d:%02d",
                    year, month + 1, dayOfMonth, hourOfday, minute1);
                    listener.onDateTimeSelected(selectedDateTime);
        }, hour, minute, true);

        timePicker.show();
    }




}
