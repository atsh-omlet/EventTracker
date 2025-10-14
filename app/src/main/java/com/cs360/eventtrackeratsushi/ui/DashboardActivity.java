package com.cs360.eventtrackeratsushi.ui;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.R;
import com.cs360.eventtrackeratsushi.adapter.EventAdapter;
import com.cs360.eventtrackeratsushi.util.DateTimePickerHelper;
import com.cs360.eventtrackeratsushi.viewmodel.DashboardViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Objects;


/**
 * Activity that displays the dashboard for events
 * handles loading evens and scheduling sms reminders
 */
public class DashboardActivity extends AppCompatActivity
    implements EventAdapter.OnDeleteClickListener, EventAdapter.OnItemLongClickListener{

    private EventAdapter eventAdapter;

    private DashboardViewModel dashboardViewModel;


    /**
     *  triggered when activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dashboardViewModel.getUsername().observe(this, username -> {
            if (username != null) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(username + "'s Events");
            }
            else{
                Objects.requireNonNull(getSupportActionBar()).setTitle("Events");
            }
        });


        // set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewEvents);
        eventAdapter = new EventAdapter(this, this);
        recyclerView.setAdapter(eventAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dashboardViewModel.getEvents().observe(this, events -> {
            if (events != null){
                eventAdapter.setEvents(events);
            }
        });


        FloatingActionButton fabAddEvent = findViewById(R.id.fabAddEvent);
        fabAddEvent.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, EventDetailsActivity.class);
            startActivity(intent);
;
        });


    }

    /**
     *  triggered when activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        dashboardViewModel.loadEvents();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    /**
     *  triggered when menu is created
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_menu, menu);


        MenuItem searchItem = menu.findItem(R.id.search);
        MenuItem filterItem = menu.findItem(R.id.filter);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                // Collapse filter when search expands
                if (filterItem.isActionViewExpanded()) {
                    filterItem.collapseActionView();
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                eventAdapter.searchEvent("");
                return true;
            }
        });

        filterItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                // Collapse search when filter expands
                if (searchItem.isActionViewExpanded()) {
                    searchItem.collapseActionView();
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                eventAdapter.filterEvents("");
                return true;
            }
        });


        SearchView searchView = (SearchView) searchItem.getActionView();
        assert searchView != null;
        searchView.setQueryHint("Search events...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                eventAdapter.searchEvent(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                eventAdapter.searchEvent(newText.trim());
                return true;
            }
        });

        SearchView filterView = (SearchView) filterItem.getActionView();
        assert filterView != null;
        filterView.setQueryHint("yyyy mm dd");
        filterView.setInputType(0);

        filterView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String normalized = query.trim()
                        .replaceAll("[^0-9/\\- ]", "")
                        .replaceAll("[/ ]", "-")
                        .replaceAll("-+$", "");

                eventAdapter.filterEvents(normalized);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                String normalized = newText.trim()
                        .replaceAll("[^0-9/\\- ]", "")
                        .replaceAll("[/ ]", "-")
                        .replaceAll("-+$", "");
                eventAdapter.filterEvents(normalized);
                return true;
            }
        });

        return true;
    }

    /**
     * triggered when settings icon is clicked
     * @param item  The menu item that was selected.
     * @return  true if the action was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    dashboardViewModel.deleteEvent(event);
                }).setNegativeButton("Cancel", null).show();
    }

    @Override
    public void onItemLongClick(Event event){
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra("event_id", event.getId());
        startActivity(intent);
    }

}
