package com.example.eventplanner.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.notifications.NotificationModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.notifications.NotificationService;
import com.example.eventplanner.data.network.services.notifications.NotificationWebSocketManager;
import com.example.eventplanner.ui.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private NotificationService service;
    private ImageButton muteButton;
    private boolean isMuted;
    private int userId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.notification_list);
        muteButton = view.findViewById(R.id.mute_button);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        adapter = new NotificationAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        service = ClientUtils.notificationService;

        loadMuteStatus();
        loadNotifications();

        muteButton.setOnClickListener(v -> toggleMuteStatus());
    }

    private void loadNotifications() {
        Call<List<NotificationModel>> call = service.getNotifications(userId);
        call.enqueue(new Callback<List<NotificationModel>>() {
            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setNotifications(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loadMuteStatus() {
        Call<Boolean> call = service.getMuteStatus(userId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isMuted = response.body();
                    updateMuteIcon();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void toggleMuteStatus() {
        boolean newState = !isMuted;
        Call<Void> call = service.toggleMuteNotifications(userId, newState);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isMuted = newState;
                    updateMuteIcon();

                    SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                    prefs.edit().putBoolean("muted", newState).apply();

                    Toast.makeText(getContext(), "Mute status changed", Toast.LENGTH_SHORT).show();

                    if (newState) {
                        NotificationWebSocketManager.disconnect();
                        Log.d("MUTE_CHANGE", "Mute status changed to: true → DISCONNECT WebSocket");
                    } else {
                        NotificationWebSocketManager.connect(requireContext().getApplicationContext(), userId, notification -> {
                            Log.d("MUTE_CHANGE", "Mute status changed to: false → CONNECT WebSocket");
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "Mute failed first: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Mute failed second: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markAllAsRead() {
        Call<Void> call = service.markAllAsRead(userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.markAllAsRead();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateMuteIcon() {
        muteButton.setImageResource(isMuted ? R.drawable.ic_bell_off : R.drawable.ic_bell_on);
    }

    @Override
    public void onStop() {
        super.onStop();
        markAllAsRead();
    }
}
