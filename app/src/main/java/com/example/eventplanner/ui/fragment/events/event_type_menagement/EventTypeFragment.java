package com.example.eventplanner.ui.fragment.events.event_type_menagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventType;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.ui.adapter.EventTypeAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventTypeFragment extends Fragment {
    private EventTypeAdapter adapter;
    private RecyclerView recyclerView;
    private Button btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_type, container, false);

        adapter = new EventTypeAdapter(new EventTypeAdapter.OnItemActionListener() {
            @Override
            public void onEdit(EventType event) {
                EditEventTypeDialog dialog = new EditEventTypeDialog(getContext(), event, new EditEventTypeDialog.OnEventEditedListener() {
                    @Override
                    public void onEdited(EventType updatedEvent) {
                        List<EventType> events = adapter.getEvents();
                        for (int i = 0; i < events.size(); i++) {
                            if (events.get(i).getId().equals(updatedEvent.getId())) {
                                events.set(i, updatedEvent);
                                break;
                            }
                        }
                        adapter.setEvents(events);
                    }

                    @Override
                    public void onDeleted(EventType deletedEvent) {
                        List<EventType> events = adapter.getEvents();
                        events.removeIf(e -> e.getId().equals(deletedEvent.getId()));
                        adapter.setEvents(events);
                    }
                });
                dialog.show();
            }

            @Override
            public void onToggle(EventType event) {
                // Pozovi API za promenu statusa
                ClientUtils.eventTypeService.toggleEventStatus(event.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Status updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error updating status", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        recyclerView = view.findViewById(R.id.recyclerEventsType);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnAdd = view.findViewById(R.id.btnAddEventType);
        btnAdd.setOnClickListener(v -> {
            AddEventTypeDialog dialog = new AddEventTypeDialog(getContext(), newEvent -> {
                List<EventType> currentList = adapter.getEvents();
                currentList.add(newEvent);
                adapter.setEvents(currentList);
                loadEventTypes();
            });
            dialog.show();
        });

        loadEventTypes();

        return view;
    }

    private void loadEventTypes() {
        ClientUtils.eventTypeService.getAllEventTypes().enqueue(new Callback<List<EventType>>() {
            @Override
            public void onResponse(Call<List<EventType>> call, Response<List<EventType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setEvents(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<EventType>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
