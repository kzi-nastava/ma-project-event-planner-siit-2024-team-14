package com.example.eventplanner.data.network.services.notifications;

import android.content.Context;
import android.util.Log;

import com.example.eventplanner.data.model.NotificationModel;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class NotificationWebSocketManager {

    private static StompClient stompClient;
    private static Disposable lifecycleDisposable;
    private static Disposable topicDisposable;
    private static final String SOCKET_URL = "ws://10.0.2.2:8080/ws";
    private static final String TAG = "NotificationWebSocket";

    public static void connect(Context context, int userId, Consumer<NotificationModel> onMessage) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, SOCKET_URL);
        stompClient.connect();

        lifecycleDisposable = stompClient.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d(TAG, "Stomp connection closed");
                            break;
                    }
                });

        topicDisposable = stompClient.topic("/topic/notifications/" + userId)
                .subscribe(stompMessage -> {
                    try {
                        String payload = stompMessage.getPayload();
                        JSONObject json = new JSONObject(payload);

                        NotificationModel notification = new NotificationModel();
                        notification.setMessage(json.optString("message", "Check your notifications"));

                        NotificationPopUp.showNotification(context, notification);

                        onMessage.accept(notification);

                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parse error", e);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe", throwable);
                });
    }

    public static void disconnect() {
        if (lifecycleDisposable != null && !lifecycleDisposable.isDisposed())
            lifecycleDisposable.dispose();
        if (topicDisposable != null && !topicDisposable.isDisposed())
            topicDisposable.dispose();
        if (stompClient != null)
            stompClient.disconnect();
    }
}
