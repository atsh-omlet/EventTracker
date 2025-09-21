package com.cs360.eventtrackeratsushi.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.R;
import com.cs360.eventtrackeratsushi.SmsWorker;
import com.cs360.eventtrackeratsushi.adapter.EventAdapter;
import com.cs360.eventtrackeratsushi.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Activity that displays the dashboard for events
 * handles loading evens and scheduling sms reminders
 */
public class DashboardActivity extends AppCompatActivity
    implements EventAdapter.OnDeleteClickListener, EventAdapter.OnItemLongClickListener{

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private DatabaseHelper dbHelper = new DatabaseHelper(this);

    private ArrayList<Event> events = new ArrayList<>();
    private String username;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        username = prefs.getString("username", null);
        userId = prefs.getInt("userId", -1);

        // set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Event Dashboard");

        // set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEvents);
        setUpEvents();
        eventAdapter = new EventAdapter(events, this, this);
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fabAddEvent = findViewById(R.id.fabAddEvent);
        fabAddEvent.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, EventDetailsActivity.class);
            startActivity(intent);
;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpEvents();
    }

    @Override
    protected void onDestroy(){
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads events from the database for the current user & schedules sms
     */
    private void setUpEvents(){
        events.clear();
        events.addAll(dbHelper.getEventsForUser(userId));
        SharedPreferences settingsPrefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String phoneNumber = settingsPrefs.getString("phone_number", "");

        // schedule reminders for each event
        for (Event event : events) {
            scheduleSmsWithWorkManager(event, phoneNumber);
        }
        if(eventAdapter != null) { // notify adapter of changes
            eventAdapter.notifyDataSetChanged();
        }
    }


    /**
     *  triggered when delete icon is clicked
     *  shows confirmation dialog before deletion
     * @param event
     */
    @Override
    public void onDeleteClick(Event event) {
        new AlertDialog.Builder(this).setTitle("Delete Event?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Delete",(dialog, which)->{
                    dbHelper.deleteEvent(event.getId());
                    setUpEvents();
                }).setNegativeButton("Cancel", null).show();
    }

    @Override
    public void onItemLongClick(Event event){
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra("event_id", event.getId());
        startActivity(intent);
    }

    /**
     * Schedules an Sms reminder using WorkManager if enabled in settings
     * @param event event to schedule reminder for
     * @param phoneNumber phone number to send sms to
     */
    private void scheduleSmsWithWorkManager(Event event, String phoneNumber) {
        SharedPreferences settingsPrefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        boolean smsEnabled = settingsPrefs.getBoolean("enable_sms_notifications", false);

        // skip if sms notifications are disabled or phone number is empty
        if (!smsEnabled || phoneNumber.isEmpty()) return;

        try {
            // parse event's date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date eventDate = sdf.parse(event.getDate());
            long delay = eventDate.getTime() - System.currentTimeMillis();
            if (delay <= 0) return; // don't schedule if time has already passed

            // prepare data for sms worker
            Data data = new Data.Builder()
                    .putString("phone", phoneNumber)
                    .putString("message", "Reminder: " + event.getTitle() + " is starting now!")
                    .build();

            // create and queue the WorkManger task
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SmsWorker.class)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build();

            WorkManager.getInstance(this).enqueue(workRequest);

        } catch (Exception e) {
            // silently fail if something goes wrong
        }
    }


}
