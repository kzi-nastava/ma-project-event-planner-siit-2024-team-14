package com.example.eventplanner.ui.adapter;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.NotificationModel;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> notifications;

    public NotificationAdapter(List<NotificationModel> notifications) {
        this.notifications = notifications;
    }

    public void setNotifications(List<NotificationModel> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView notificationText;
        public ViewHolder(View view) {
            super(view);
            notificationText = view.findViewById(R.id.notification_text);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationModel n = notifications.get(position);
        holder.notificationText.setText(n.getMessage());

        holder.itemView.setBackgroundResource(n.isRead() ? android.R.color.white : R.color.unreadBackground);
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }
}
