package com.example.eventplanner.data.network.stomp;

import android.util.Log;


import com.google.gson.Gson;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;


// should probably be an actual android service
public class StompService {
    private static final String TAG = StompService.class.getSimpleName();

    private final StompClient client;

    private static StompService instance;
    private Disposable lifecycleDisposable;



    public StompService() {
        client = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "ws://10.0.2.2:8080/ws/websocket" // TODO: Move authority to local properties
                );
    }


    public Flowable<StompMessage> subscribe(final String topic) {
        return client.topic(topic)
                .doOnSubscribe(subscription ->
                        Log.d(TAG, "Subscribed to topic: " + topic))
                .doOnNext(message ->
                        Log.d(TAG, "Received message on " + topic + ": " + message.getPayload()))
                .doOnError(error ->
                        Log.e(TAG, "Error on topic " + topic, error));
    }


    public Completable sendMessage(final String destination, Object message) {
        final String payload = message instanceof String
                ? (String) message
                : new Gson().toJson(message);

        return client.send(destination, payload)
                .doOnComplete(() ->
                        Log.d(TAG, "Message sent to " + destination + ": " + payload))
                .doOnError(throwable ->
                        Log.e(TAG, "Failed to send message to " + destination, throwable));
    }


    public void connect() {
        if (!client.isConnected()) {
            attachLifecycleListener();
            client.connect();
        }
    }


    private void attachLifecycleListener() {
        if (lifecycleDisposable != null && !lifecycleDisposable.isDisposed()) {
            lifecycleDisposable.dispose();
        }

        lifecycleDisposable = client.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    switch (event.getType()) {
                        case OPENED:
                            Log.d(TAG, "Stomp connected");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp error", event.getException());
                            break;
                        case CLOSED:
                            Log.d(TAG, "Stomp disconnected");
                            break;
                    }
                });
    }


    public void disconnect() {
        if (lifecycleDisposable != null) {
            lifecycleDisposable.dispose();
        }

        client.disconnect();
    }


    @Deprecated
    public static synchronized StompService getInstance() {
        if (instance == null)
            instance = new StompService();

        return instance;
    }

}
