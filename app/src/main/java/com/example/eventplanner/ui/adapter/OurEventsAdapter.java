package com.example.eventplanner.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;

import org.json.JSONObject;

import java.util.List;

public class OurEventsAdapter extends RecyclerView.Adapter<OurEventsAdapter.EventViewHolder> {

    private final List<JSONObject> events;
    private final Context context;
    private final OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(JSONObject event);
    }

    public OurEventsAdapter(Context context, List<JSONObject> events, OnEventClickListener listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        JSONObject obj = events.get(position);

        try {
            holder.organizerName.setText(obj.getString("organizerFirstName") + " " + obj.getString("organizerLastName"));
            holder.eventTitle.setText(obj.getString("name"));
            holder.eventDescription.setText(obj.getString("description"));

            String baseUrl = "http://10.0.2.2:8080/";
            String fullProfileImageUrl = baseUrl + obj.getString("organizerProfilePicture");
            Glide.with(context)
                    .load(fullProfileImageUrl)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(holder.organizerImage);

            String fullImageUrl = baseUrl + obj.getString("imageUrl");
            Glide.with(context)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.card_placeholder)
                    .error(R.drawable.card_placeholder)
                    .into(holder.eventImage);

            holder.viewMore.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(obj);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateData(List<JSONObject> newEvents) {
        events.clear();
        events.addAll(newEvents);
        notifyDataSetChanged();
    }


    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView organizerName, eventTitle, eventDescription;
        ImageView organizerImage, eventImage;
        Button viewMore;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            organizerName = itemView.findViewById(R.id.organizer_name);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDescription = itemView.findViewById(R.id.event_description);
            organizerImage = itemView.findViewById(R.id.organizer_image);
            eventImage = itemView.findViewById(R.id.event_image);
            viewMore = itemView.findViewById(R.id.view_more_button);
        }
    }
}
