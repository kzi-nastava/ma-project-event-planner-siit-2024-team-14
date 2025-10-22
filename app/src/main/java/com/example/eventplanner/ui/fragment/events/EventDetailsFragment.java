package com.example.eventplanner.ui.fragment.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.EventModel;
import com.example.eventplanner.data.model.events.ToggleFavoriteResponse;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.events.EventService;
import com.example.eventplanner.ui.fragment.budget.EventBudgetFragment;
import com.example.eventplanner.ui.fragment.chat.ChatFragment;
import com.example.eventplanner.ui.fragment.profiles.ViewOrganizerProfileFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment {

    public static EventDetailsFragment newInstance(int eventId) {
        Bundle b = new Bundle();
        b.putInt("eventId", eventId);
        EventDetailsFragment f = new EventDetailsFragment();
        f.setArguments(b);
        return f;
    }

    // Views
    private TextView nameText, locationText, dateText, descriptionText, organizerText;
    private ImageView eventImage;
    private ImageButton favoriteButton;
    private Button chatButton;

    // State
    private int eventId;
    private int loggedUserId;
    private EventModel event;
    private boolean isFavorite = false;

    private static final String BASE = "http://10.0.2.2:8080/api/";

    public EventDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        nameText = v.findViewById(R.id.event_name);
        locationText = v.findViewById(R.id.event_location);
        dateText = v.findViewById(R.id.event_date);
        descriptionText = v.findViewById(R.id.event_description);
        organizerText = v.findViewById(R.id.event_organizer);

        eventImage = v.findViewById(R.id.event_image);
        favoriteButton = v.findViewById(R.id.favorite_button);
        chatButton = v.findViewById(R.id.chat_button);

        if (getArguments() != null) {
            eventId = getArguments().getInt("eventId", -1);
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        loggedUserId = prefs.getInt("userId", -1);

        if (eventId != -1) {
            fetchEventDetails(eventId);

            // Child budget fragment
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.budget_layout, EventBudgetFragment.newInstance(eventId))
                    .commit();
        }

        // Favorite toggle
        if (favoriteButton != null) {
            favoriteButton.setOnClickListener(v1 -> {
                if (loggedUserId <= 0 || eventId <= 0) {
                    Toast.makeText(requireContext(), "Please log in to favorite events.", Toast.LENGTH_SHORT).show();
                    return;
                }
                toggleFavorite();
            });
        }

        // Chat with organizer
        if (chatButton != null) {
            chatButton.setOnClickListener(v12 -> {
                if (event != null && event.getOrganizerId() > 0) {
                    ChatFragment chat = ChatFragment.newInstance(event.getOrganizerId());
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.home_page_fragment, chat)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(requireContext(), "Organizer not available.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchEventDetails(int id) {
        EventService eventApi = ClientUtils.eventService;
        eventApi.getEventById(id).enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    event = response.body();
                    displayEvent(event);
                    if (loggedUserId > 0) checkIsFavorite();
                } else {
                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEvent(EventModel e) {
        nameText.setText(nullTo(e.getName(), "Event"));
        locationText.setText("Location: " + nullTo(e.getLocation(), "Unknown"));
        dateText.setText("From " + nullTo(e.getStartDate(), "-") + " to " + nullTo(e.getEndDate(), "-"));
        descriptionText.setText(nullTo(e.getDescription(), "No description"));

        // Organizer text + link to profile (if not me)
        String fn = safe(e.getOrganizerFirstName());
        String ln = safe(e.getOrganizerLastName());
        String fullName = (fn + " " + ln).trim();
        fullName = TextUtils.isEmpty(fullName) ? "Organizer" : fullName;

        if (e.getOrganizerId() == loggedUserId) {
            organizerText.setText(fullName);
            organizerText.setOnClickListener(null);
        } else {
            organizerText.setText(Html.fromHtml("<u>" + fullName + "</u>"));
            organizerText.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt("organizerId", e.getOrganizerId());

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

        // Event image
        if (eventImage != null) {
            String url = BASE + "events/get-photo/" + e.getId();
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.profile_placeholder)
                    .centerCrop()
                    .into(eventImage);
        }

        renderFavoriteIcon(); // reflect current state
    }

    /** Query server favorites to set the initial star state */
    private void checkIsFavorite() {
        if (loggedUserId <= 0 || event == null) return;
        ClientUtils.organizerService.getFavoriteEvents(loggedUserId)
                .enqueue(new Callback<List<EventModel>>() {
                    @Override public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            isFavorite = false;
                            for (EventModel m : resp.body()) {
                                if (m.getId() == eventId) { isFavorite = true; break; }
                            }
                            renderFavoriteIcon();
                        }
                    }
                    @Override public void onFailure(Call<List<EventModel>> call, Throwable t) {
                        // ignore
                    }
                });
    }

    /** POST toggle on server, then update icon */
    private void toggleFavorite() {
        ClientUtils.organizerService.toggleFavoriteEvent(loggedUserId, eventId)
                .enqueue(new Callback<ToggleFavoriteResponse>() {
                    @Override public void onResponse(Call<ToggleFavoriteResponse> call, Response<ToggleFavoriteResponse> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            isFavorite = resp.body().isFavorite;
                            renderFavoriteIcon();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update favorite", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<ToggleFavoriteResponse> call, Throwable t) {
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderFavoriteIcon() {
        if (favoriteButton == null) return;
        favoriteButton.setImageResource(isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        favoriteButton.setContentDescription(isFavorite ? "Unfavorite" : "Favorite");
    }

    public static class FavoriteStatus {
        public boolean isFavorite;
    }

    // helpers
    private static String nullTo(String s, String fb) { return s == null ? fb : s; }
    private static String safe(String s) { return s == null ? "" : s; }
}
