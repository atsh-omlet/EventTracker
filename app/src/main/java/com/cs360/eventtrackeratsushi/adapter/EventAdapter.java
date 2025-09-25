package com.cs360.eventtrackeratsushi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs360.eventtrackeratsushi.model.Event;
import com.cs360.eventtrackeratsushi.R;

import java.util.List;

/**
 * Adpater for displaying list of Event objects in a RecyclerView
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> events;
    private OnDeleteClickListener deleteListener;
    private OnItemLongClickListener longClickListener;

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
        this.events = events;
        notifyDataSetChanged();
    }

    /**
     *  Inflates item layout and creates the ViewHolder
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Bind event data to ViewHolder UI
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventName.setText(event.getTitle());
        holder.eventDate.setText(event.getFormattedDate());

        // Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(event);
            }
        });

        // Item long click
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(event);
                return true;
            }
            return false;
        });
    }

    /**
     *
     * @return total number of events in list
     */
    @Override
    public int getItemCount() {
        return events.size();
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
}

