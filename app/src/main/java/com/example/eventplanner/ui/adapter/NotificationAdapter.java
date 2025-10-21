package com.example.eventplanner.ui.adapter;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.notifications.NotificationModel;
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

    public void markAllAsRead() {
        for (NotificationModel n : notifications) {
            n.setRead(true);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView notificationText;
        View notificationCard;

        public ViewHolder(View view) {
            super(view);
            notificationText = view.findViewById(R.id.notification_text);
            notificationCard = view.findViewById(R.id.notification_card);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationModel n = notifications.get(position);
        holder.notificationText.setText(n.getMessage());

        if (n.getRead()) {
            holder.notificationCard.setBackgroundResource(R.drawable.bg_notification_read);
        } else {
            holder.notificationCard.setBackgroundResource(R.drawable.bg_notification_unread);
        }
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }
}

