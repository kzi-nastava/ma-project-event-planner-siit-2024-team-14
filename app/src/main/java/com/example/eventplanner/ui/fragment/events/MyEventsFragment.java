package com.example.eventplanner.ui.fragment.events;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.events.EventService;
import com.example.eventplanner.ui.adapter.EventAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private ImageButton createEventButton;
    private int page = 0;
    private final int pageSize = 10;
    private int totalPages = 0;

    private EventService eventService;

    public MyEventsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerViewMyEvents);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        createEventButton = view.findViewById(R.id.btnCreateEvent);
        createEventButton.setOnClickListener(v -> {
            CreateEventFragment createEventFragment = new CreateEventFragment();

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_page_fragment, createEventFragment)
                    .addToBackStack(null)
                    .commit();
        });

        eventService = ClientUtils.eventService;

        loadMyEvents();
    }

    private void loadMyEvents() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        Integer userId = prefs.getInt("userId", -1);
        if (userId == null) return;

        eventService.getEventsByOrganizer(userId).enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<EventModel>> call, @NonNull Response<List<EventModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventModel> events = response.body();
                    totalPages = (int) Math.ceil(events.size() / (double) pageSize);
                    adapter = new EventAdapter(events);
                    recyclerView.setAdapter(adapter);
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
