package com.example.eventplanner.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventType;

import java.util.ArrayList;
import java.util.List;

public class EventTypeAdapter extends RecyclerView.Adapter<EventTypeAdapter.ViewHolder> {

    private List<EventType> eventList = new ArrayList<>();
    private final OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(EventType event);
        void onToggle(EventType event);
    }

    public EventTypeAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    public void setEvents(List<EventType> events) {
        this.eventList = events;
        notifyDataSetChanged();
    }

    public List<EventType> getEvents() {
        return eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_type, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventType event = eventList.get(position);
        holder.name.setText(event.getName());
        holder.description.setText(event.getDescription());
        holder.btnActivate.setText(event.isActive() ? "Deactivate" : "Activate");

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(event));
        holder.btnActivate.setOnClickListener(v -> listener.onToggle(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        Button btnEdit, btnActivate;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.eventName);
            description = itemView.findViewById(R.id.eventDesc);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnActivate = itemView.findViewById(R.id.btnActivate);
        }
    }
}
