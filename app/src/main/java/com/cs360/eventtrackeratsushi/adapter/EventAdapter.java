package com.cs360.eventtrackeratsushi.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.R;
import com.cs360.eventtrackeratsushi.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying list of Event objects in a RecyclerView
 */
public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = "EventAdapter";
    private final int VIEW_TYPE_HEADER = 0;
    private final int VIEW_TYPE_EVENT = 1;
    private final List<Event> events = new ArrayList<>();
    private final List<Object> displayedItems = new ArrayList<>();
    private final OnDeleteClickListener deleteListener;
    private final OnItemLongClickListener longClickListener;
    private final DateUtils dateUtils = new DateUtils();

    /**
     * interface for callback when event's delete button is pressed
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(Event event);
    }

    /**
     * Interface for callback when event is long pressed
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(Event event);
    }

    /**
     *  Constructor for EventAdapter
     * @param deleteListener
     * @param longClickListener
     */
    public EventAdapter(OnDeleteClickListener deleteListener,
                        OnItemLongClickListener longClickListener) {
        this.deleteListener = deleteListener;
        this.longClickListener = longClickListener;
    }

    public void setEvents(List<Event> events){
        this.events.clear();
        this.events.addAll(events);
        displayedItems.clear();
        rebuildDisplayedItems(events);
    }

    /**
     * Inflates item layout and creates the ViewHolder
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_EVENT) {
            View view = inflater.inflate(R.layout.item_event, parent, false);
            return new EventViewHolder(view);
        } else { // viewType == VIEW_TYPE_HEADER
            View view = inflater.inflate(R.layout.item_date_header, parent, false);
            return new DateHeaderViewHolder(view);
        }
    }

    /**
     * Bind event data to ViewHolder UI
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_EVENT) {
            Event event = (Event) displayedItems.get(position);
            EventViewHolder eventHolder = (EventViewHolder) holder;

            eventHolder.eventName.setText(event.getTitle());
            eventHolder.eventDate.setText(dateUtils.formatTime(event.getDate()));

            // Delete button click
            eventHolder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(event);
                }
            });

            // Item long click
            eventHolder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(event);
                    return true;
                }
                return false;
            });
        } else if (viewType == VIEW_TYPE_HEADER) {
            String date = (String) displayedItems.get(position);
            DateHeaderViewHolder headerHolder = (DateHeaderViewHolder) holder;
            headerHolder.dateHeader.setText(date);
        }



    }

    @Override
    public int getItemViewType(int position) {
        return (displayedItems.get(position) instanceof Event ) ? VIEW_TYPE_EVENT : VIEW_TYPE_HEADER;
    }

    /**
     * Search for events based on title
     * @param text  The search query
     */
    public void searchEvent(String text){
        displayedItems.clear();
        List<Event> filteredList = new ArrayList<>();
        if (text.trim().isEmpty()||text.length()<2){
            rebuildDisplayedItems(events);
        }
        else {
            String query = text.toLowerCase();
            for (Event event : events){
                if (event.getTitle().toLowerCase().contains(query)){
                    filteredList.add(event);
                }
            }
        }
        rebuildDisplayedItems(filteredList);
    }

    /**
     * Filter events based on date
     * @param date  The date to filter by date
     */
    public void filterEvents (String date) {
        displayedItems.clear();
        List<Event> filteredList = new ArrayList<>();

        if (date.trim().isEmpty()||date.length()<4){
            rebuildDisplayedItems(events);
            return;
        }
        for (Event event : events){
            if (dateUtils.formatDate(event.getDate()).startsWith(date)){
                filteredList.add(event);
            }
        }
        rebuildDisplayedItems(filteredList);
    }

    /**
     *
     * @return total number of events in list
     */
    @Override
    public int getItemCount() {
        return displayedItems.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate;
        ImageView btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void rebuildDisplayedItems(List<Event> events){
        String lastDate = null;
        for (Event event: events){
            String eventDate = dateUtils.formatWeekday(event.getDate());
            if (!eventDate.equals(lastDate)){
                Log.d(TAG, "eventDate: " + eventDate);
                Log.d(TAG, "lastDate: " + lastDate);
                Log.d(TAG, "Adding header for eventDate: " + eventDate);
                if (dateUtils.isToday(event.getDate())) {
                    displayedItems.add(eventDate+ " (Today)");
                } else {
                    displayedItems.add(eventDate);
                }
                lastDate = eventDate;
            }
            displayedItems.add(event);
        }
        notifyDataSetChanged();
    }


    public static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateHeader;
        public DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateHeader = itemView.findViewById(R.id.item_date_header);
        }
    }
}

