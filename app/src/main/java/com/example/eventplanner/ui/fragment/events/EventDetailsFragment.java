package com.example.eventplanner.ui.fragment.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.events.EventService;
import com.example.eventplanner.ui.fragment.profiles.ViewOrganizerProfileFragment;
import com.example.eventplanner.ui.fragment.budget.EventBudgetFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment {
    private TextView nameText, locationText, dateText, descriptionText, organizerText;
    private int eventId;
    private int loggedUserId;

    public EventDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameText = view.findViewById(R.id.event_name);
        locationText = view.findViewById(R.id.event_location);
        dateText = view.findViewById(R.id.event_date);
        descriptionText = view.findViewById(R.id.event_description);
        organizerText = view.findViewById(R.id.event_organizer);

        if (getArguments() != null) {
            eventId = getArguments().getInt("eventId", -1);
        }
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        loggedUserId = prefs.getInt("userId", -1);

        if (eventId != -1) {
            fetchEventDetails(eventId);

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.budget_layout, EventBudgetFragment.newInstance(eventId))
                    .commit();
        }
    }

    private void fetchEventDetails(int id) {
        EventService eventApi = ClientUtils.eventService;
        eventApi.getEventById(id).enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EventModel event = response.body();
                    displayEvent(event);
                }
            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEvent(EventModel event) {
        nameText.setText(event.getName());
        locationText.setText("Location: " + event.getLocation());
        dateText.setText("From " + event.getStartDate() + " to " + event.getEndDate());
        descriptionText.setText(event.getDescription());

        String fullName = event.getOrganizerFirstName() + " " + event.getOrganizerLastName();
        if (event.getOrganizerId() == loggedUserId) {
            organizerText.setText(fullName);
        } else {
            organizerText.setText(Html.fromHtml("<u>" + fullName + "</u>"));
            organizerText.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt("organizerId", event.getOrganizerId());

                ViewOrganizerProfileFragment fragment = new ViewOrganizerProfileFragment();
                fragment.setArguments(bundle);

                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_page_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            });

        }
    }
}
