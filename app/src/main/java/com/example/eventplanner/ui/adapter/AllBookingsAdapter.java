package com.example.eventplanner.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.services.BookingServiceRequestModel;

import java.util.List;

public class AllBookingsAdapter extends RecyclerView.Adapter<AllBookingsAdapter.ViewHolder> {
    private List<BookingServiceRequestModel> bookings;

    public AllBookingsAdapter(List<BookingServiceRequestModel> bookings) {
        this.bookings = bookings;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceText, eventText, dateText, timeText, durationText, statusText;

        public ViewHolder(View view) {
            super(view);
            serviceText = view.findViewById(R.id.service_text);
            eventText = view.findViewById(R.id.event_text);
            dateText = view.findViewById(R.id.date_text);
            timeText = view.findViewById(R.id.time_text);
            durationText = view.findViewById(R.id.duration_text);
            statusText = view.findViewById(R.id.status_text);
        }
    }

    @NonNull
    @Override
    public AllBookingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_bookings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingServiceRequestModel booking = bookings.get(position);
        holder.serviceText.setText("Service: " + booking.getService());
        holder.eventText.setText("Event: " + booking.getEvent());
        holder.dateText.setText("Date: " + booking.getBookingDate());
        holder.timeText.setText("Time: " + booking.getStartTime());
        holder.durationText.setText("Duration: " + booking.getDuration() + " min");
        holder.statusText.setText("Status: " + booking.getConfirmed());

        // Dodaj boju statusa
        switch (booking.getConfirmed()) {
            case "APPROVED":
                holder.statusText.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "REJECTED":
                holder.statusText.setTextColor(Color.parseColor("#F44336"));
                break;
            case "PENDING":
                holder.statusText.setTextColor(Color.parseColor("#FF9800"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }
}
