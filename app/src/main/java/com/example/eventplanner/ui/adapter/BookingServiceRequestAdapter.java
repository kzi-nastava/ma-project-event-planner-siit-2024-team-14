package com.example.eventplanner.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.BookingServiceRequestModel;

import java.util.List;

public class BookingServiceRequestAdapter extends RecyclerView.Adapter<BookingServiceRequestAdapter.ViewHolder> {
    private List<BookingServiceRequestModel> requests;
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onApprove(BookingServiceRequestModel request);
        void onReject(BookingServiceRequestModel request);
    }

    public BookingServiceRequestAdapter(List<BookingServiceRequestModel> requests, OnActionClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceText, eventText, dateText, timeText, durationText;
        Button approveButton, rejectButton;

        public ViewHolder(View view) {
            super(view);
            serviceText = view.findViewById(R.id.service_text);
            eventText = view.findViewById(R.id.event_text);
            dateText = view.findViewById(R.id.date_text);
            timeText = view.findViewById(R.id.time_text);
            durationText = view.findViewById(R.id.duration_text);
            approveButton = view.findViewById(R.id.approve_button);
            rejectButton = view.findViewById(R.id.reject_button);
        }
    }

    @Override
    public BookingServiceRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_service_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookingServiceRequestModel request = requests.get(position);
        holder.serviceText.setText("Service: " + request.getService());
        holder.eventText.setText("Event: " + request.getEvent());
        holder.dateText.setText("Date: " + request.getBookingDate());
        holder.timeText.setText("Time: " + request.getStartTime());
        holder.durationText.setText("Duration: " + request.getDuration() + " min");

        holder.approveButton.setOnClickListener(v -> listener.onApprove(request));
        holder.rejectButton.setOnClickListener(v -> listener.onReject(request));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
