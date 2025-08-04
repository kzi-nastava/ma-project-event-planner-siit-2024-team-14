package com.example.eventplanner.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.ui.adapter.OurEventsAdapter;
import com.example.eventplanner.ui.adapter.OurSolutionAdapter;
import com.example.eventplanner.ui.fragment.solutions.SolutionDetailsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private LinearLayout cardContainer;
    private RecyclerView recyclerViewOurEvents;
    private RecyclerView recyclerViewOurSolutions;
    private Button loadMoreButton;
    private Button loadMoreButton2;
    private List<JSONObject> originalEvents = new ArrayList<>();
    private List<JSONObject> filteredEvents = new ArrayList<>();

    private List<JSONObject> originalSolutions = new ArrayList<>();
    private List<JSONObject> filteredSolutions = new ArrayList<>();
    private OurEventsAdapter adapter;
    private OurSolutionAdapter adapterSolution;
    private int itemsToShow = 4;
    private EditText etSearch, etStartDate, etEndDate;
    private Spinner spinnerCategory, spinnerLocation;
    private Button btnApplyFilters;
    private ImageButton btnToggleFilters;
    private LinearLayout filterContainer;
    private LinearLayout cardContainerServices;
    private Button btnApplyFilters2;
    private ImageButton btnToggleFilters2;
    private EditText etServiceSearch, etServiceStartDate, etServiceEndDate, etMinPrice, etMaxPrice;
    private Spinner spinnerSolutionLocation, spinnerSolutionCategory, spinnerSolutionType;
    private LinearLayout filterContainer2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        cardContainer = rootView.findViewById(R.id.card_container);

        recyclerViewOurEvents = rootView.findViewById(R.id.recycler_view_our_events);
        loadMoreButton = rootView.findViewById(R.id.load_more_button);

        fetchHottestEvents();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerViewOurEvents.setLayoutManager(gridLayoutManager);
        recyclerViewOurEvents.setHasFixedSize(true);
        adapter = new OurEventsAdapter(requireContext(), new ArrayList<>(), event -> {
            int eventId = event.optInt("id");
                EventDetailsFragment fragment = new EventDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("eventId", eventId);
                fragment.setArguments(bundle);

                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_page_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
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
                fetchFilteredEventsLocally();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        btnApplyFilters.setOnClickListener(v -> fetchFilteredEvents());

        fetchCategories();
        fetchLocations();

        cardContainerServices = rootView.findViewById(R.id.card_container_services);
        fetchTopSolutions();

        recyclerViewOurSolutions = rootView.findViewById(R.id.recycler_view_our_solutions);
        loadMoreButton2 = rootView.findViewById(R.id.load_more_button2);

        GridLayoutManager solutionLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerViewOurSolutions.setLayoutManager(solutionLayoutManager);
        recyclerViewOurSolutions.setHasFixedSize(true);
        adapterSolution = new OurSolutionAdapter(requireContext(), new ArrayList<>(), solution -> {
            try {
                String solutionType = solution.optString("solutionType");
                int solutionId = solution.optInt("id");
                Fragment detailsFragment = SolutionDetailsFragment.newInstance(solutionId, solutionType);
                FragmentTransition.to(detailsFragment, requireActivity(), R.id.home_page_fragment, true);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to open details", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewOurSolutions.setAdapter(adapterSolution);

        loadMoreButton2.setOnClickListener(v -> {
            itemsToShow += 4;
            displayOurSolutions();
        });

        fetchAllSolutions();

        etServiceSearch = rootView.findViewById(R.id.service_search);
        etServiceStartDate = rootView.findViewById(R.id.service_start_date);
        etServiceEndDate = rootView.findViewById(R.id.service_end_date);
        etMinPrice = rootView.findViewById(R.id.min_price);
        etMaxPrice = rootView.findViewById(R.id.max_price);
        spinnerSolutionLocation = rootView.findViewById(R.id.spinner_solution_location);
        spinnerSolutionCategory = rootView.findViewById(R.id.spinner_solution_category);
        spinnerSolutionType = rootView.findViewById(R.id.spinner_solution_type);
        btnApplyFilters2 = rootView.findViewById(R.id.btn_apply_filters2);
        btnToggleFilters2 = rootView.findViewById(R.id.btn_toggle_filters2);
        filterContainer2 = rootView.findViewById(R.id.filter_container2);

        // Date pickeri
        etServiceStartDate.setOnClickListener(v -> showDatePickerDialog(etServiceStartDate));
        etServiceEndDate.setOnClickListener(v -> showDatePickerDialog(etServiceEndDate));

        // Toggle filtera
        btnToggleFilters2.setOnClickListener(v -> {
            filterContainer2.setVisibility(
                    filterContainer2.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
            );
        });

        btnApplyFilters2.setOnClickListener(v -> fetchFilteredSolutions());

        // Search bar
        etServiceSearch.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSolutionsLocally();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        // Fetch vrednosti za spinnere
        fetchSolutionCategories();
        fetchSolutionLocations();
        fetchSolutionTypes();

        return rootView;
    }

    // --- FETCH HOTTEST EVENTS ---
    private void fetchHottestEvents() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userCity = prefs.getString("userCity", "Novi Sad");
        String url = "http://10.0.2.2:8080/api/events/top5?city=" + userCity;

        int userIdInt = prefs.getInt("userId", -1);
        if (userIdInt != -1) {
            url += "&userId=" + userIdInt;
        }
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                this::populateHottestCards,
                Throwable::printStackTrace
        );

        queue.add(request);
    }

    private void populateHottestCards(JSONArray events) {
        cardContainer.removeAllViews();
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

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userIdInt = prefs.getInt("userId", -1);
        if (userIdInt != -1) {
            url += "?userId=" + userIdInt;
        }
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
                int eventId = obj.optInt("id");
                EventDetailsFragment fragment = new EventDetailsFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("eventId", eventId);
                fragment.setArguments(bundle);

                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_page_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchFilteredEvents() {
        String baseUrl = "http://10.0.2.2:8080/api/events/filter";

        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendQueryParameter("page", "0");
        builder.appendQueryParameter("size", "100");

        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        String selectedLocation = spinnerLocation.getSelectedItem().toString();

        if (!startDate.isEmpty()) {
            builder.appendQueryParameter("startDate", startDate);
        }
        if (!endDate.isEmpty()) {
            builder.appendQueryParameter("endDate", endDate);
        }

        if (!selectedCategory.equalsIgnoreCase("All categories")) {
            builder.appendQueryParameter("category", selectedCategory);
        }
        if (!selectedLocation.equalsIgnoreCase("All Cities")) {
            builder.appendQueryParameter("location", selectedLocation);
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId != -1) {
            builder.appendQueryParameter("userId", String.valueOf(userId));
        }

        String finalUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null,
                response -> {
                    JSONArray content = response.optJSONArray("content");
                    originalEvents.clear();

                    if (content != null) {
                        for (int i = 0; i < content.length(); i++) {
                            originalEvents.add(content.optJSONObject(i));
                        }
                    }
                    filteredEvents = new ArrayList<>(originalEvents);
                    itemsToShow = 4;
                    displayOurEvents();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error fetching filtered events", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
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

    private void fetchTopSolutions() {
        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userCity = prefs.getString("userCity", "Novi Sad");

        String url = "http://10.0.2.2:8080/api/solutions/top5?city=" + userCity;

        int userIdInt = prefs.getInt("userId", -1);
        if (userIdInt != -1) {
            url += "&userId=" + userIdInt;
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                this::populateSolutionCards,
                Throwable::printStackTrace
        );

        queue.add(request);
    }


    private void populateSolutionCards(JSONArray solutions) {
        cardContainerServices.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < solutions.length(); i++) {
            try {
                JSONObject obj = solutions.getJSONObject(i);
                View card = inflater.inflate(R.layout.item_solution_card, cardContainerServices, false);
                fillSolutionCard(card, obj);
                cardContainerServices.addView(card);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fillSolutionCard(View card, JSONObject obj) {
        try {
            TextView providerName = card.findViewById(R.id.provider_name);
            TextView providerRole = card.findViewById(R.id.provider_role);
            ImageView providerImage = card.findViewById(R.id.provider_image);
            ImageView solutionImage = card.findViewById(R.id.solution_image);
            TextView solutionTitle = card.findViewById(R.id.solution_title);
            TextView solutionDesc = card.findViewById(R.id.solution_description);
            Button viewMoreBtn = card.findViewById(R.id.view_more_button_solution);

            providerName.setText(obj.optString("providerCompanyName", "Unknown"));
            providerRole.setText("Service and product provider");
            solutionTitle.setText(obj.optString("name", ""));
            solutionDesc.setText(obj.optString("description", ""));

            String baseUrl = "http://10.0.2.2:8080/";
            String imageUrl = baseUrl + obj.optString("imageUrl", "");

            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.card_placeholder)
                    .error(R.drawable.card_placeholder)
                    .into(solutionImage);

            // ako ima provider sliku - Glide za providerImage (opciono)
            viewMoreBtn.setOnClickListener(v -> {
                String solutionType = obj.optString("solutionType");
                int solutionId = obj.optInt("id");

                Fragment detailsFragment = SolutionDetailsFragment.newInstance(solutionId, solutionType);
                FragmentTransition.to(detailsFragment, requireActivity(), R.id.home_page_fragment, true);
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchAllSolutions() {
        String url = "http://10.0.2.2:8080/api/solutions/all";

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userIdInt = prefs.getInt("userId", -1);
        if (userIdInt != -1) {
            url += "?userId=" + userIdInt;
        }

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    originalSolutions.clear();
                    for (int i = 0; i < response.length(); i++) {
                        originalSolutions.add(response.optJSONObject(i));
                    }
                    filteredSolutions = new ArrayList<>(originalSolutions);
                    itemsToShow = 4;
                    displayOurSolutions();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error fetching services", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }

    private void displayOurSolutions() {
        int count = Math.min(itemsToShow, filteredSolutions.size());
        List<JSONObject> sublist = filteredSolutions.subList(0, count);
        adapterSolution.updateData(sublist);

        if (itemsToShow >= filteredSolutions.size()) {
            loadMoreButton2.setVisibility(View.GONE);
        } else {
            loadMoreButton2.setVisibility(View.VISIBLE);
        }
    }


    private void fetchFilteredSolutions() {
        String baseUrl = "http://10.0.2.2:8080/api/solutions/filter";

        Uri.Builder builder = Uri.parse(baseUrl).buildUpon();
        builder.appendQueryParameter("page", "0");
        builder.appendQueryParameter("size", "100");

        String search = etServiceSearch.getText().toString().trim();
        String startDate = etServiceStartDate.getText().toString().trim();
        String endDate = etServiceEndDate.getText().toString().trim();
        String category = spinnerSolutionCategory.getSelectedItem().toString();
        String location = spinnerSolutionLocation.getSelectedItem().toString();
        String type = spinnerSolutionType.getSelectedItem().toString();
        String minPrice = etMinPrice.getText().toString().trim();
        String maxPrice = etMaxPrice.getText().toString().trim();

        if (!startDate.isEmpty()) builder.appendQueryParameter("startDate", startDate);
        if (!endDate.isEmpty()) builder.appendQueryParameter("endDate", endDate);
        if (!category.equalsIgnoreCase("All categories")) builder.appendQueryParameter("category", category);
        if (!location.equalsIgnoreCase("All Cities")) builder.appendQueryParameter("location", location);
        if (!type.equalsIgnoreCase("All types")) builder.appendQueryParameter("type", type);
        if (!minPrice.isEmpty()) builder.appendQueryParameter("minPrice", minPrice);
        if (!maxPrice.isEmpty()) builder.appendQueryParameter("maxPrice", maxPrice);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);
        if (userId != -1) {
            builder.appendQueryParameter("userId", String.valueOf(userId));
        }

        String finalUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null,
                response -> {
                    JSONArray content = response.optJSONArray("content");
                    originalSolutions.clear();

                    if (content != null) {
                        for (int i = 0; i < content.length(); i++) {
                            originalSolutions.add(content.optJSONObject(i));
                        }
                    }

                    filteredSolutions = new ArrayList<>(originalSolutions);
                    itemsToShow = 4;
                    displayOurSolutions();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error fetching filtered solutions", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void fetchSolutionCategories() {
        String url = "http://10.0.2.2:8080/api/solutions/categories";
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
                    spinnerSolutionCategory.setAdapter(adapter);
                },
                Throwable::printStackTrace
        );

        queue.add(request);
    }

    private void fetchSolutionLocations() {
        String url = "http://10.0.2.2:8080/api/solutions/locations";
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
                    spinnerSolutionLocation.setAdapter(adapter);
                },
                Throwable::printStackTrace
        );

        queue.add(request);
    }

    private void fetchSolutionTypes() {
        List<String> types = new ArrayList<>();
        types.add("All types");
        types.add("Service");
        types.add("Product");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSolutionType.setAdapter(adapter);
    }

    private void filterSolutionsLocally() {
        String query = etServiceSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
        filteredSolutions.clear();

        if (query.isEmpty()) {
            filteredSolutions.addAll(originalSolutions);
        } else {
            for (JSONObject obj : originalSolutions) {
                String name = obj.optString("name", "").toLowerCase(Locale.ROOT);
                String desc = obj.optString("description", "").toLowerCase(Locale.ROOT);
                String provider = obj.optString("providerCompanyName", "").toLowerCase(Locale.ROOT);

                if (name.contains(query) || desc.contains(query) || provider.contains(query)) {
                    filteredSolutions.add(obj);
                }
            }
        }

        itemsToShow = 4;
        displayOurSolutions();
    }

    private void fetchFilteredEventsLocally() {
        String query = etSearch.getText().toString().trim().toLowerCase(Locale.ROOT);
        filteredEvents.clear();

        if (query.isEmpty()) {
            filteredEvents.addAll(originalEvents);
        } else {
            for (JSONObject obj : originalEvents) {
                String name = obj.optString("name", "").toLowerCase(Locale.ROOT);
                String desc = obj.optString("description", "").toLowerCase(Locale.ROOT);
                String organizerFirst = obj.optString("organizerFirstName", "").toLowerCase(Locale.ROOT);
                String organizerLast = obj.optString("organizerLastName", "").toLowerCase(Locale.ROOT);
                String location = obj.optString("location", "").toLowerCase(Locale.ROOT);
                String type = obj.optString("eventType", "").toLowerCase(Locale.ROOT);

                if (
                        name.contains(query) ||
                                desc.contains(query) ||
                                organizerFirst.contains(query) ||
                                organizerLast.contains(query) ||
                                (organizerFirst + " " + organizerLast).contains(query) ||
                                location.contains(query) ||
                                type.contains(query)
                ) {
                    filteredEvents.add(obj);
                }
            }
        }

        itemsToShow = 4;
        displayOurEvents();
    }

}
