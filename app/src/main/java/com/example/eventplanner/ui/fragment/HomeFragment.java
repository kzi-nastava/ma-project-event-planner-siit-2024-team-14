package com.example.eventplanner.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.ui.adapter.OurEventsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private LinearLayout cardContainer;  // za hottest events (horizontal scroll)
    private RecyclerView recyclerViewOurEvents;
    private Button loadMoreButton;

    private List<JSONObject> allEvents = new ArrayList<>();
    private OurEventsAdapter adapter;

    private int itemsToShow = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        cardContainer = rootView.findViewById(R.id.card_container);

        recyclerViewOurEvents = rootView.findViewById(R.id.recycler_view_our_events);
        loadMoreButton = rootView.findViewById(R.id.load_more_button);

        // Hottest events - isto kao pre
        fetchHottestEvents();

        // Our events - RecyclerView setup
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerViewOurEvents.setLayoutManager(gridLayoutManager);
        recyclerViewOurEvents.setHasFixedSize(true);
        adapter = new OurEventsAdapter(requireContext(), new ArrayList<>(), event -> {
            // TODO: Detalji događaja za klik na Our Events karticu
        });
        recyclerViewOurEvents.setAdapter(adapter);

        fetchOurEvents();

        loadMoreButton.setOnClickListener(v -> {
            itemsToShow += 4;
            displayOurEvents();
        });

        return rootView;
    }

    // --- FETCH HOTTEST EVENTS ---
    private void fetchHottestEvents() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userCity = prefs.getString("userCity", "Novi Sad");
        String url = "http://10.0.2.2:8080/api/events/top5?city=" + userCity;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                this::populateHottestCards,
                Throwable::printStackTrace
        );

        queue.add(request);
    }

    private void populateHottestCards(JSONArray events) {
        cardContainer.removeAllViews();  // obriši prethodne ako ih ima
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < events.length(); i++) {
            try {
                JSONObject obj = events.getJSONObject(i);
                View card = inflater.inflate(R.layout.item_event_card, cardContainer, false);

                fillCard(card, obj);

                cardContainer.addView(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // --- FETCH OUR EVENTS ---
    private void fetchOurEvents() {
        String url = "http://10.0.2.2:8080/api/events/all";

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    allEvents.clear();
                    for (int i = 0; i < response.length(); i++) {
                        allEvents.add(response.optJSONObject(i));
                    }
                    displayOurEvents();
                },
                Throwable::printStackTrace
        );

        queue.add(request);
    }

    private void displayOurEvents() {
        int count = Math.min(itemsToShow, allEvents.size());
        List<JSONObject> sublist = allEvents.subList(0, count);
        adapter.updateData(sublist);

        if (itemsToShow >= allEvents.size()) {
            loadMoreButton.setVisibility(View.GONE);
        } else {
            loadMoreButton.setVisibility(View.VISIBLE);
        }
    }

    // --- POPUNI KARTICU  ---
    private void fillCard(View card, JSONObject obj) {
        try {
            TextView organizerName = card.findViewById(R.id.organizer_name);
            TextView eventTitle = card.findViewById(R.id.event_title);
            TextView eventDescription = card.findViewById(R.id.event_description);
            ImageView organizerImage = card.findViewById(R.id.organizer_image);
            ImageView eventImage = card.findViewById(R.id.event_image);
            Button viewMore = card.findViewById(R.id.view_more_button);

            organizerName.setText(obj.getString("organizerFirstName") + " " + obj.getString("organizerLastName"));
            eventTitle.setText(obj.getString("name"));
            eventDescription.setText(obj.getString("description"));

            String baseUrl = "http://10.0.2.2:8080/";
            String fullProfileImageUrl = baseUrl + obj.getString("organizerProfilePicture");
            Glide.with(requireContext())
                    .load(fullProfileImageUrl)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .into(organizerImage);

            String fullImageUrl = baseUrl + obj.getString("imageUrl");
            Glide.with(requireContext())
                    .load(fullImageUrl)
                    .placeholder(R.drawable.card_placeholder)
                    .error(R.drawable.card_placeholder)
                    .into(eventImage);

            viewMore.setOnClickListener(v -> {
                // TODO: Otvori detalje događaja
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
