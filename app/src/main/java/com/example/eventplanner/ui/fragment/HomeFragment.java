package com.example.eventplanner.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private LinearLayout cardContainer;  // za hottest events (horizontal scroll)
    private RecyclerView recyclerViewOurEvents;
    private Button loadMoreButton;


    private List<JSONObject> originalEvents = new ArrayList<>();
    private List<JSONObject> filteredEvents = new ArrayList<>();

    private OurEventsAdapter adapter;

    private int itemsToShow = 4;

    private EditText etSearch, etStartDate, etEndDate;
    private Spinner spinnerCategory, spinnerLocation;
    private Button btnApplyFilters;
    private ImageButton btnToggleFilters;
    private LinearLayout filterContainer;

    private String selectedCategory = "";
    private String selectedLocation = "";

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

        etSearch = rootView.findViewById(R.id.et_search);
        etStartDate = rootView.findViewById(R.id.et_start_date);
        etEndDate = rootView.findViewById(R.id.et_end_date);
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));
        spinnerCategory = rootView.findViewById(R.id.spinner_category);
        spinnerLocation = rootView.findViewById(R.id.spinner_location);
        btnApplyFilters = rootView.findViewById(R.id.btn_apply_filters);
        btnToggleFilters = rootView.findViewById(R.id.btn_toggle_filters);
        filterContainer = rootView.findViewById(R.id.filter_container);
        btnToggleFilters.setOnClickListener(v -> {
            filterContainer.setVisibility(
                    filterContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
            );
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        btnApplyFilters.setOnClickListener(v -> filterEvents());

        fetchCategories();
        fetchLocations();
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
                    originalEvents.clear();
                    for (int i = 0; i < response.length(); i++) {
                        originalEvents.add(response.optJSONObject(i));
                    }
                    filteredEvents = new ArrayList<>(originalEvents);
                    itemsToShow = 4;
                    displayOurEvents();
                },
                Throwable::printStackTrace
        );

        queue.add(request);
    }


    private void displayOurEvents() {
        int count = Math.min(itemsToShow, filteredEvents.size());
        List<JSONObject> sublist = filteredEvents.subList(0, count);
        adapter.updateData(sublist);

        if (itemsToShow >= filteredEvents.size()) {
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

    private void filterEvents() {
        String searchTerm = etSearch.getText().toString().toLowerCase().trim();
        String startDate = etStartDate.getText().toString();
        String endDate = etEndDate.getText().toString();
        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        String selectedLocation = spinnerLocation.getSelectedItem().toString();

        List<JSONObject> filtered = new ArrayList<>();
        for (JSONObject event : originalEvents) {
            try {
                String name = event.getString("name").toLowerCase();
                String desc = event.getString("description").toLowerCase();
                String firstName = event.getString("organizerFirstName").toLowerCase();
                String lastName = event.getString("organizerLastName").toLowerCase();
                String eventLocation = event.optString("location", "");
                String eventCategory = event.optString("category", "");
                String eventStartDate = event.optString("startDate", "");
                String eventEndDate = event.optString("endDate", "");

                boolean matchesSearch = searchTerm.isEmpty() || name.contains(searchTerm)
                        || desc.contains(searchTerm)
                        || firstName.contains(searchTerm)
                        || lastName.contains(searchTerm);

                boolean matchesLocation = selectedLocation.equals("All Cities") || selectedLocation.equals(eventLocation);
                boolean matchesCategory = selectedCategory.equals("All categories") || selectedCategory.equals(eventCategory);

                boolean matchesDate = (startDate.isEmpty() || eventStartDate.compareTo(startDate) >= 0)
                        && (endDate.isEmpty() || eventEndDate.compareTo(endDate) <= 0);

                if (matchesSearch && matchesLocation && matchesCategory && matchesDate) {
                    filtered.add(event);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        filteredEvents = filtered;
        itemsToShow = 4;
        displayOurEvents();
    }

    private void fetchLocations() {
        String url = "http://10.0.2.2:8080/api/events/locations";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> locations = new ArrayList<>();
                    locations.add("All Cities");
                    for (int i = 0; i < response.length(); i++) {
                        locations.add(response.optString(i));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, locations);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLocation.setAdapter(adapter);
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }

    private void fetchCategories() {
        String url = "http://10.0.2.2:8080/api/events/categories";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> categories = new ArrayList<>();
                    categories.add("All categories");

                    for (int i = 0; i < response.length(); i++) {
                        categories.add(response.optString(i));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }

    private void showDatePickerDialog(EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    String formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    targetEditText.setText(formatted);
                },
                year, month, day
        );
        datePickerDialog.show();
    }



}
