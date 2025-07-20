package com.example.eventplanner.data.network.services.notifications;

import com.example.eventplanner.data.model.notifications.NotificationModel;
import java.util.List;
import retrofit2.*;

public class NotificationService {

    private final NotificationApiClient api;

    public NotificationService(NotificationApiClient api) {
        this.api = api;
    }

    public void getNotifications(int userId, Callback<List<NotificationModel>> callback) {
        api.getNotifications(userId).enqueue(callback);
    }

    public void markAllAsRead(int userId, Callback<Void> callback) {
        api.markAllAsRead(userId).enqueue(callback);
    }

    public void toggleMuteNotifications(int userId, boolean muted, Callback<Void> callback) {
        api.toggleMuteNotifications(userId, muted).enqueue(callback);
    }

    public void getMuteStatus(int userId, Callback<Boolean> callback) {
        api.getMuteStatus(userId).enqueue(callback);
    }
}
