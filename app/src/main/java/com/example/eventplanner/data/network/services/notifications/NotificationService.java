package com.example.eventplanner.data.network.services.notifications;
import com.example.eventplanner.data.model.notifications.NotificationModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface NotificationService {

    @GET("notifications")
    Call<List<NotificationModel>> getNotifications(@Query("userId") int userId);

    @PUT("notifications/mark-all-as-read")
    Call<Void> markAllAsRead(@Query("userId") int userId);

    @PUT("notifications/mute-notifications")
    Call<Void> toggleMuteNotifications(@Query("userId") int userId, @Query("muted") boolean muted);

    @GET("notifications/mute-notifications/status")
    Call<Boolean> getMuteStatus(@Query("userId") int userId);
}
