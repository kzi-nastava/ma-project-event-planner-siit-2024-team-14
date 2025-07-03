package com.example.eventplanner.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.eventplanner.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private LinearLayout cardContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        cardContainer = rootView.findViewById(R.id.card_container);
        fetchEvents();
        return rootView;
    }

    private void fetchEvents() {
        String city = "Novi Sad"; // promeni po potrebi
        String url = "http://10.0.2.2:8080/api/events/top5?city=" + city;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                this::populateCards,
                error -> error.printStackTrace()
        );

        queue.add(request);
    }

    private void populateCards(JSONArray events) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < events.length(); i++) {
            try {
                JSONObject obj = events.getJSONObject(i);
                View card = inflater.inflate(R.layout.item_top_event_card, cardContainer, false);

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
                    // TODO: Otvori detalje događaja, koristi event ID ako ti treba
                });

                cardContainer.addView(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
