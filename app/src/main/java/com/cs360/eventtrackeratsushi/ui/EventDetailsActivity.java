package com.cs360.eventtrackeratsushi.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.cs360.eventtrackeratsushi.R;
import com.cs360.eventtrackeratsushi.util.DateTimePickerHelper;
import com.cs360.eventtrackeratsushi.viewmodel.EventDetailsViewModel;

import java.util.Objects;


/**
 * Activity for creating a new event or editing an existing event
 */
public class EventDetailsActivity extends AppCompatActivity {

    // UI components
    private EditText etEventName, etDate;
    private EventDetailsViewModel eventDetailsViewModel;
    private int eventId = -1; // current event ID, -1 if new event

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        etEventName = findViewById(R.id.etEventName);
        etDate = findViewById(R.id.etDate);
        etDate.setFocusable(false);
        etDate.setClickable(true);
        Button btnSaveEvent = findViewById(R.id.btnSaveEvent);

        eventDetailsViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);


        // set up tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId != -1) { // existing event, set edit event as title
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Event");
            eventDetailsViewModel.loadEvent(eventId);
        } else { // new event, set new event as title
            Objects.requireNonNull(getSupportActionBar()).setTitle("New Event");
        }

        // Observe ViewModel fields
        eventDetailsViewModel.getEventName().observe(this, etEventName::setText);
        eventDetailsViewModel.getEventDate().observe(this, etDate::setText);

        eventDetailsViewModel.getSaveSuccess().observe(this, success ->{
            if (success != null && success){
                Toast.makeText(this, (eventId == -1 ? "Event Created":"Event Updated"),
                        Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
            }
        });

        eventDetailsViewModel.getErrorMessage().observe(this, errorMessage ->{
            if (errorMessage != null){
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


        // show date picker when etDate is clicked
        etDate.setOnClickListener(v ->
                DateTimePickerHelper.showDatePicker(this, dateTime -> {
                    etDate.setText(dateTime);
                }));
        // save event details
        btnSaveEvent.setOnClickListener(v -> {
            eventDetailsViewModel.setEventName(etEventName.getText().toString().trim());
            eventDetailsViewModel.setEventDate(etDate.getText().toString().trim());
            eventDetailsViewModel.saveEvent();
        });
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


}