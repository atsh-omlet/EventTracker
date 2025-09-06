package com.cs360.eventtrackeratsushi;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for creating a new event or editing an existing event
 */
public class EventDetailsActivity extends AppCompatActivity {

    // UI components
    private EditText etEventName, etDate;
    private Button btnSaveEvent;
    private DatabaseHelper dbHelper;
    private int eventId = -1; // current event ID, -1 if new event
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        etEventName = findViewById(R.id.etEventName);
        etDate = findViewById(R.id.etDate);
        etDate.setFocusable(false);
        etDate.setClickable(true);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        dbHelper = new DatabaseHelper(this);

        // Retrieve use id from shared preferences
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        // set up tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId != -1) { // existing event, set edit event as title
            getSupportActionBar().setTitle("Edit Event");
            loadEventData();
        } else { // new event, set new event as title
            getSupportActionBar().setTitle("New Event");
        }

        // show date picker when etDate is clicked
        etDate.setOnClickListener(v -> showDatePicker());
        // save event detaisl
        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    /**
     * load event deatils from database
     * */
    private void loadEventData() {
        Event event = dbHelper.getEvent(eventId);
        if (event != null) {
            etEventName.setText(event.getTitle());
            etDate.setText(event.getDate());
        }
    }


    /**
     * Show date picker dialog to select event date. Past dates are restricted.
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
            // show tiem picker to select time after date is selcted
            showTimePicker(year, month, dayOfMonth);
            ;
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); //prevent past dates
        datePicker.show();
    }

    /**
     * Showtime picker dialog to select event time
     * @param year year selected
     * @param month month selected
     * @param dayOfMonth day selected
     */
    private void showTimePicker(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(this, (view,
                                                                  hourOfday, minute1)->{
            String selectedDateTime = String.format(Locale.getDefault(),
                    "%04d-%02d-%02d %02d:%02d",
                    year, month + 1, dayOfMonth, hourOfday, minute1);
            etDate.setText(selectedDateTime);
        }, hour, minute, true);
        timePicker.show();
    }

    /**
     * Save event to database after validating
     */
    private void saveEvent(){
        String eventName = etEventName.getText().toString().trim();
        String eventDate = etDate.getText().toString().trim();

        if (eventName.isEmpty() || eventDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventId == -1) {
            // create new event
            boolean result = dbHelper.addEvent(eventName, eventDate, userId);
            if (result) {
                Toast.makeText(this, "Event created", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        }
        else {
            // update existing event
            boolean result = dbHelper.updateEvent(eventId, eventName, eventDate);
            if (result) {
                Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy(){
        dbHelper.close();
        super.onDestroy();
    }


}