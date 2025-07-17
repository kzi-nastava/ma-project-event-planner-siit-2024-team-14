package com.example.eventplanner.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.BookingServiceModel;
import com.example.eventplanner.data.model.EventModel;
import com.example.eventplanner.data.model.ServiceModel;
import com.example.eventplanner.data.network.ApiClient;
import com.example.eventplanner.data.network.services.events.EventService;
import com.example.eventplanner.data.network.services.solutions.BookingServiceService;
import com.example.eventplanner.data.network.services.solutions.ServicesService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceReservationFragment extends Fragment {

    private static final String ARG_SERVICE_ID = "serviceId";

    private Spinner eventSpinner;
    private EditText dateInput;
    private Spinner durationSpinner;
    private Spinner startTimeSpinner;
    private Button confirmBookingButton;

    private int serviceId;

    private List<EventModel> userEvents = new ArrayList<>();
    private int selectedEventId = -1;
    private String selectedEventStartDate = "";

    private ServiceModel serviceDetails;

    private List<Integer> durationOptions = new ArrayList<>();
    private List<String> availableStartTimes = new ArrayList<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static ServiceReservationFragment newInstance(int serviceId) {
        ServiceReservationFragment fragment = new ServiceReservationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SERVICE_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }

    public ServiceReservationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_reservation, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            serviceId = getArguments().getInt(ARG_SERVICE_ID);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        eventSpinner = view.findViewById(R.id.event_spinner);
        dateInput = view.findViewById(R.id.date_input);
        durationSpinner = view.findViewById(R.id.duration_spinner);
        startTimeSpinner = view.findViewById(R.id.start_time_spinner);
        confirmBookingButton = view.findViewById(R.id.confirmBookingButton);

        dateInput.setFocusable(false);

        loadServiceDetails(serviceId);
        loadUserEvents();

        setupDatePicker();
        setupEventSpinnerListener();
        setupDurationSpinnerListener();

        confirmBookingButton.setOnClickListener(v -> submitReservation());
    }

    private void loadServiceDetails(int serviceId) {
        ServicesService serviceApi = ApiClient.getServiceApi();
        serviceApi.getServiceById(serviceId).enqueue(new Callback<ServiceModel>() {
            @Override
            public void onResponse(Call<ServiceModel> call, Response<ServiceModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    serviceDetails = response.body();
                    prepareDurationOptions();
                } else {
                    Toast.makeText(getContext(), "Failed to load service details", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServiceModel> call, Throwable t) {
                Toast.makeText(getContext(), "Error loading service details: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadUserEvents() {
        int userId = requireActivity().getSharedPreferences("MyAppPrefs", 0).getInt("userId", -1);

        EventService eventApi = ApiClient.getEventService();
        eventApi.getEventsByOrganizer(userId).enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userEvents = response.body();
                    populateEventSpinner();
                } else {
                    Toast.makeText(getContext(), "Failed to load user events", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Error loading events: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateEventSpinner() {
        List<String> eventNames = new ArrayList<>();
        for (EventModel e : userEvents) {
            eventNames.add(e.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, eventNames);
        eventSpinner.setAdapter(adapter);
    }

    private void setupDatePicker() {
        dateInput.setOnClickListener(v -> {
            if (selectedEventId == -1) {
                Toast.makeText(getContext(), "Please select an event first", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar calendar = Calendar.getInstance();
            try {
                if (!TextUtils.isEmpty(dateInput.getText())) {
                    calendar.setTime(dateFormat.parse(dateInput.getText().toString()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        String chosenDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        if (validateDate(chosenDate)) {
                            dateInput.setText(chosenDate);
                            fetchAvailableStartTimes(chosenDate, getSelectedDuration());
                        } else {
                            Toast.makeText(getContext(), "Date is outside allowed reservation period.", Toast.LENGTH_LONG).show();
                        }
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void setupEventSpinnerListener() {
        eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EventModel selectedEvent = userEvents.get(position);
                selectedEventId = selectedEvent.getId();
                selectedEventStartDate = selectedEvent.getStartDate();

                if (validateDate(selectedEventStartDate)) {
                    dateInput.setText(selectedEventStartDate);
                    fetchAvailableStartTimes(selectedEventStartDate, getSelectedDuration());
                } else {
                    dateInput.setText("");
                    availableStartTimes.clear();
                    updateStartTimeSpinner();
                    Toast.makeText(getContext(), "Cannot book for this event based on reservation period.", Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                selectedEventId = -1;
                selectedEventStartDate = "";
                dateInput.setText("");
                availableStartTimes.clear();
                updateStartTimeSpinner();
            }
        });
    }

    private void prepareDurationOptions() {
        durationOptions.clear();

        if (serviceDetails == null) return;

        Integer fixedDur = convertDurationToMinutes(serviceDetails.getDuration());
        Integer minDur = convertDurationToMinutes(serviceDetails.getMinDuration());
        Integer maxDur = convertDurationToMinutes(serviceDetails.getMaxDuration());

        if (fixedDur != null && fixedDur > 0) {
            durationOptions.add(fixedDur);
        } else if (minDur != null && maxDur != null && maxDur >= minDur) {
            for (int d = minDur; d <= maxDur; d += 30) {
                durationOptions.add(d);
            }
        }

        List<String> durationLabels = new ArrayList<>();
        for (Integer d : durationOptions) {
            durationLabels.add(d + " minutes");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, durationLabels);
        durationSpinner.setAdapter(adapter);

        if (!durationOptions.isEmpty()) {
            durationSpinner.setSelection(0);
        }
    }

    private void setupDurationSpinnerListener() {
        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String date = dateInput.getText().toString();
                if (!TextUtils.isEmpty(date)) {
                    fetchAvailableStartTimes(date, getSelectedDuration());
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private int getSelectedDuration() {
        int pos = durationSpinner.getSelectedItemPosition();
        if (pos >= 0 && pos < durationOptions.size()) {
            return durationOptions.get(pos);
        }
        return 0;
    }

    private boolean validateDate(String selectedDate) {
        if (selectedEventId == -1 || TextUtils.isEmpty(selectedDate) || serviceDetails == null) {
            return false;
        }

        try {
            Calendar today = Calendar.getInstance();
            Calendar eventStart = Calendar.getInstance();
            eventStart.setTime(dateFormat.parse(selectedEventStartDate));

            Calendar chosenDate = Calendar.getInstance();
            chosenDate.setTime(dateFormat.parse(selectedDate));

            if (chosenDate.before(today)) return false;
            if (eventStart.before(today)) return false;

            int reservationPeriodDays = convertDurationToDays(serviceDetails.getReservationPeriod());

            Calendar minAllowedBookingDate = (Calendar) eventStart.clone();
            minAllowedBookingDate.add(Calendar.DAY_OF_YEAR, -reservationPeriodDays);

            // Date must be between minAllowedBookingDate and eventStart date (inclusive)
            return !chosenDate.before(minAllowedBookingDate) && !chosenDate.after(eventStart);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fetchAvailableStartTimes(String date, int duration) {
        if (TextUtils.isEmpty(date) || duration <= 0) {
            availableStartTimes.clear();
            updateStartTimeSpinner();
            return;
        }

        BookingServiceService bookingApi = ApiClient.getBookingServiceApi();
        bookingApi.getAvailableStartTimes(serviceId, date, duration).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableStartTimes = response.body();
                    updateStartTimeSpinner();
                } else {
                    Toast.makeText(getContext(), "Failed to load available start times", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Error fetching start times: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStartTimeSpinner() {
        List<String> items = availableStartTimes.isEmpty() ? List.of("No available times") : availableStartTimes;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, items);
        startTimeSpinner.setAdapter(adapter);
        startTimeSpinner.setEnabled(!availableStartTimes.isEmpty());
    }

    private void submitReservation() {
        if (selectedEventId == -1) {
            Toast.makeText(getContext(), "Please select an event", Toast.LENGTH_SHORT).show();
            return;
        }
        String date = dateInput.getText().toString();
        if (TextUtils.isEmpty(date)) {
            Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (availableStartTimes.isEmpty() || startTimeSpinner.getSelectedItem() == null ||
                startTimeSpinner.getSelectedItem().equals("No available times")) {
            Toast.makeText(getContext(), "Please select a valid start time", Toast.LENGTH_SHORT).show();
            return;
        }
        String startTime = startTimeSpinner.getSelectedItem().toString();
        int duration = getSelectedDuration();

        BookingServiceModel request = new BookingServiceModel(
                serviceId,
                selectedEventId,
                date,
                startTime,
                duration,
                serviceDetails != null ? serviceDetails.getReservationType() : ""
        );

        ApiClient.getBookingServiceApi()
                .reserveService(request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Reservation successful!", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        } else {
                            Toast.makeText(getContext(), "Failed to reserve: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private Integer convertDurationToMinutes(String duration) {
        if (duration == null || duration.isEmpty()) return null;
        // Examples: PT30M, PT1H, PT90M, PT2H30M (if complex, simplify to first match)
        try {
            if (duration.contains("H") && duration.contains("M")) {
                // Handle combined e.g. PT2H30M -> 150 min
                int hours = 0, minutes = 0;
                String hPart = duration.replaceAll(".*PT(\\d+)H.*", "$1");
                String mPart = duration.replaceAll(".*H(\\d+)M", "$1");
                try {
                    hours = Integer.parseInt(hPart);
                } catch (Exception ignored) {}
                try {
                    minutes = Integer.parseInt(mPart);
                } catch (Exception ignored) {}
                return hours * 60 + minutes;
            }
            if (duration.contains("H")) {
                String h = duration.replaceAll("PT(\\d+)H", "$1");
                return Integer.parseInt(h) * 60;
            }
            if (duration.contains("M")) {
                String m = duration.replaceAll("PT(\\d+)M", "$1");
                return Integer.parseInt(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int convertDurationToDays(String duration) {
        if (duration == null || duration.isEmpty()) return 0;
        try {
            if (duration.startsWith("P") && duration.endsWith("D")) {
                String d = duration.replaceAll("P(\\d+)D", "$1");
                return Integer.parseInt(d);
            }
            if (duration.contains("H")) {
                String h = duration.replaceAll("PT(\\d+)H", "$1");
                int hours = Integer.parseInt(h);
                return (int) Math.ceil(hours / 24.0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
