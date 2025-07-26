package com.example.eventplanner.ui.adapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.ui.fragment.EventDetailsFragment;
import java.util.List;
import android.content.Context;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<EventModel> eventList;
    private final String baseUrl = "http://10.0.2.2:8080/";

    public EventAdapter(List<EventModel> events) {
        this.eventList = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventModel event = eventList.get(position);

        Context context = holder.itemView.getContext();

        holder.name.setText(event.getName());
        holder.description.setText(event.getDescription());
        holder.organizerName.setText(event.getOrganizerFirstName() + event.getOrganizerLastName());

        String organizerImageUrl = baseUrl + event.getOrganizerProfilePicture();
        Glide.with(context)
                .load(organizerImageUrl)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.profile_placeholder)
                .into(holder.organizerImage);

        String eventImageUrl = baseUrl + event.getImageUrl();
        Glide.with(holder.itemView.getContext())
                .load(eventImageUrl)
                .placeholder(R.drawable.card_placeholder)
                .error(R.drawable.card_placeholder)
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            EventModel e = eventList.get(holder.getAdapterPosition());

            EventDetailsFragment detailsFragment = new EventDetailsFragment();
            Bundle args = new Bundle();
            args.putLong("eventId", e.getId());
            detailsFragment.setArguments(args);

            FragmentActivity activity = (FragmentActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_page_fragment, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, organizerName;
        ImageView image, organizerImage;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.event_title);
            description = itemView.findViewById(R.id.event_description);
            image = itemView.findViewById(R.id.event_image);
            organizerImage = itemView.findViewById(R.id.organizer_image);
            organizerName = itemView.findViewById(R.id.organizer_name);

        }
    }
}
