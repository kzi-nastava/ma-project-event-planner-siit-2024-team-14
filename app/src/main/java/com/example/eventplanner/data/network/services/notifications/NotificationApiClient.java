package com.example.eventplanner.data.network.services.notifications;

import com.example.eventplanner.data.model.notifications.NotificationModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface NotificationApiClient {
    @GET("/api/notifications")
    Call<List<NotificationModel>> getNotifications(@Query("userId") int userId);

    @PUT("/api/notifications/mark-all-as-read")
    Call<Void> markAllAsRead(@Query("userId") int userId);

    @PUT("/api/notifications/mute-notifications")
    Call<Void> toggleMuteNotifications(@Query("userId") int userId, @Query("muted") boolean muted);

    @GET("/api/notifications/mute-notifications/status")
    Call<Boolean> getMuteStatus(@Query("userId") int userId);
}
