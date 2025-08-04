package com.example.eventplanner.ui.fragment.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.events.EventService;
import com.example.eventplanner.ui.adapter.EventAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinedEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView noEventsText;
    private EventAdapter adapter;
    private EventService eventService;

    private final int pageSize = 10;
    private int totalPages = 0;

    public JoinedEventsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joined_events, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMyEvents);
        noEventsText = view.findViewById(R.id.textNoJoinedEvents);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        eventService = ClientUtils.eventService;

        loadEvents();
    }

    private void loadEvents() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId == -1) return;

        eventService.getJoinedEvents(userId).enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<EventModel>> call, @NonNull Response<List<EventModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventModel> events = response.body();

                    if (events.isEmpty()) {
                        noEventsText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noEventsText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        totalPages = (int) Math.ceil(events.size() / (double) pageSize);
                        adapter = new EventAdapter(events);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<EventModel>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
