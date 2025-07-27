package com.example.eventplanner.ui.fragment;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.CategoriesEtModel;
import com.example.eventplanner.data.model.events.CreateEventModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.events.EventService;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.*;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventFragment extends Fragment {

    private EditText etName, etDescription, etGuestNumber, etLocation, etStartDate, etEndDate;
    private Spinner spinnerEventType;
    private LinearLayout layoutCategories;
    private Button btnUploadPhoto, btnCreate;
    private TextView tvErrorMessage;

    private List<CategoriesEtModel> categories = new ArrayList<>();
    private List<String> selectedCategories = new ArrayList<>();

    private List<CategoriesEtModel> eventTypes = new ArrayList<>();
    private ArrayAdapter<String> eventTypeAdapter;

    private Uri selectedImageUri = null;
    private Integer organizerId;

    private Calendar startDateCalendar;
    private Calendar endDateCalendar;

    private CategoriesEtModel selectedEventType = null;

    private static final int PICK_IMAGE_REQUEST = 1;

    private EventService eventService; // Initialized externally or via Retrofit builder

    private Spinner privacySpinner;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        etName = view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        etGuestNumber = view.findViewById(R.id.etGuestNumber);
        etLocation = view.findViewById(R.id.etLocation);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        etLocation = view.findViewById(R.id.etLocation);

        spinnerEventType = view.findViewById(R.id.spinnerEventType);
        layoutCategories = view.findViewById(R.id.layoutCategories);
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto);
        btnCreate = view.findViewById(R.id.btnCreate);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        organizerId = prefs.getInt("userId", -1);

        eventService = ClientUtils.eventService;

        privacySpinner = view.findViewById(R.id.privacySpinner);

        ArrayAdapter<CharSequence> privacyAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.privacy_types,
                android.R.layout.simple_spinner_item
        );
        privacyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        privacySpinner.setAdapter(privacyAdapter);


        setupEventTypeSpinner();
        setupDatePickers();
        setupPhotoUpload();
        setupCreateButton();

        loadEventTypes();

        return view;
    }

    private void setupEventTypeSpinner() {
        eventTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(eventTypeAdapter);

        spinnerEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < eventTypes.size()) {
                    selectedEventType = eventTypes.get(position);
                    if ("All".equals(selectedEventType.getName())) {
                        loadAllCategories();
                    } else {
                        loadCategoriesForEventType(selectedEventType.getName());
                    }
                } else {
                    selectedEventType = null;
                    categories.clear();
                    updateCategoriesUI();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEventType = null;
                categories.clear();
                updateCategoriesUI();
            }
        });

    }

    private void loadEventTypes() {
        eventService.getAllEventTypes().enqueue(new Callback<List<CategoriesEtModel>>() {
            @Override
            public void onResponse(Call<List<CategoriesEtModel>> call, Response<List<CategoriesEtModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes = response.body();

                    CategoriesEtModel all = new CategoriesEtModel();
                    all.setName("All");
                    eventTypes.add(0, all);

                    List<String> names = new ArrayList<>();
                    for (CategoriesEtModel t : eventTypes) names.add(t.getName());

                    eventTypeAdapter.clear();
                    eventTypeAdapter.addAll(names);
                    eventTypeAdapter.notifyDataSetChanged();

                    spinnerEventType.setSelection(0);
                    selectedEventType = eventTypes.get(0);
                    loadCategoriesForEventType(selectedEventType.getName());
                }
            }
            @Override
            public void onFailure(Call<List<CategoriesEtModel>> call, Throwable t) {
                tvErrorMessage.setText("Error loading event types");
            }
        });
    }


    private void loadAllCategories() {
        eventService.getAllCategories().enqueue(new Callback<List<CategoriesEtModel>>() {
            @Override
            public void onResponse(Call<List<CategoriesEtModel>> call, Response<List<CategoriesEtModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    updateCategoriesUI();
                }
            }
            @Override
            public void onFailure(Call<List<CategoriesEtModel>> call, Throwable t) {
                tvErrorMessage.setText("Error loading categories");
            }
        });
    }


    private void loadCategoriesForEventType(String eventTypeName) {
        eventService.getServicesAndProducts(eventTypeName).enqueue(new Callback<List<CategoriesEtModel>>() {
            @Override
            public void onResponse(Call<List<CategoriesEtModel>> call, Response<List<CategoriesEtModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    updateCategoriesUI();
                }
            }
            @Override
            public void onFailure(Call<List<CategoriesEtModel>> call, Throwable t) {
                tvErrorMessage.setText("Error loading categories");
            }
        });
    }

    private void updateCategoriesUI() {
        layoutCategories.removeAllViews();
        selectedCategories.clear();

        for (CategoriesEtModel cat : categories) {
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(cat.getName());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedCategories.contains(cat.getName())) selectedCategories.add(cat.getName());
                } else {
                    selectedCategories.remove(cat.getName());
                }
            });
            layoutCategories.addView(checkBox);
        }
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(true));
        etEndDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth);

            String formattedDate = dateFormat.format(selectedCal.getTime());
            if (isStartDate) {
                etStartDate.setText(formattedDate);
                startDateCalendar = selectedCal;
            } else {
                etEndDate.setText(formattedDate);
                endDateCalendar = selectedCal;
            }
        }, y, m, d);

        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.show();
    }

    private void setupPhotoUpload() {
        btnUploadPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            btnUploadPhoto.setText("Photo Selected");
        }
    }

    private void setupCreateButton() {
        btnCreate.setOnClickListener(v -> {
            if (!validateForm()) return;
            sendCreateEventRequest();
        });
    }

    private boolean validateForm() {
        if (etName.getText().toString().trim().isEmpty()) {
            tvErrorMessage.setText("Name is required");
            return false;
        }
        if (etDescription.getText().toString().trim().isEmpty()) {
            tvErrorMessage.setText("Description is required");
            return false;
        }
        if (selectedEventType == null) {
            tvErrorMessage.setText("Select event type");
            return false;
        }
        if (selectedCategories.isEmpty()) {
            tvErrorMessage.setText("Select at least one category");
            return false;
        }
        if (etGuestNumber.getText().toString().trim().isEmpty()) {
            tvErrorMessage.setText("Guest number is required");
            return false;
        }
        if (etLocation.getText().toString().trim().isEmpty()) {
            tvErrorMessage.setText("Location is required");
            return false;
        }
        if (startDateCalendar == null) {
            tvErrorMessage.setText("Start date is required");
            return false;
        }
        if (endDateCalendar == null) {
            tvErrorMessage.setText("End date is required");
            return false;
        }
        if (endDateCalendar.before(startDateCalendar)) {
            tvErrorMessage.setText("End date cannot be before start date");
            return false;
        }
        tvErrorMessage.setText("");
        return true;
    }

    private void sendCreateEventRequest() {
        tvErrorMessage.setText("");

        String privacyType = privacySpinner.getSelectedItem().toString();
        // Pravi JSON za dto deo
        Map<String, Object> dtoMap = new HashMap<>();
        dtoMap.put("name", etName.getText().toString().trim());
        dtoMap.put("description", etDescription.getText().toString().trim());
        dtoMap.put("categories", selectedCategories);
        dtoMap.put("guestNumber", etGuestNumber.getText().toString().trim());
        dtoMap.put("type", privacyType);
        dtoMap.put("location", etLocation.getText().toString().trim());
        dtoMap.put("startDate", apiDateFormat.format(startDateCalendar.getTime()));
        dtoMap.put("endDate", apiDateFormat.format(endDateCalendar.getTime()));
        dtoMap.put("eventType", selectedEventType.getName());
        dtoMap.put("organizer", organizerId);

        String jsonDto = new Gson().toJson(dtoMap);

        RequestBody dtoBody = RequestBody.create(MediaType.parse("application/json"), jsonDto);

        MultipartBody.Part photoPart = null;
        if (selectedImageUri != null) {
            photoPart = prepareFilePart("photo", selectedImageUri);
        } else {
            // Retrofit ne voli null MultipartBody.Part, pa šaljemo prazni deo ili preskačemo (ovo zavisi od servera)
            // Ako možeš, napravi overload ili nullable @Part MultipartBody.Part photo na backendu
        }

        eventService.createEvent(dtoBody, photoPart).enqueue(new Callback<CreateEventModel>() {
            @Override
            public void onResponse(Call<CreateEventModel> call, Response<CreateEventModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Event created successfully!", Toast.LENGTH_LONG).show();
                    clearForm();

                    if ("CLOSED".equalsIgnoreCase(privacyType) && response.body() != null) {
                        int eventId = response.body().getId();

                        String guestNumberStr = etGuestNumber.getText().toString().trim();
                        int maxGuests = 0;
                        if (!guestNumberStr.isEmpty()) {
                            try {
                                maxGuests = Integer.parseInt(guestNumberStr);
                            } catch (NumberFormatException e) {
                                maxGuests = 0;
                            }
                        }

                        InvitationFragment fragment = InvitationFragment.newInstance(eventId, maxGuests, null);

                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.home_page_fragment, fragment)
                                .addToBackStack(null)
                                .commit();
                    }

                } else if (response.code() == 409) {
                    tvErrorMessage.setText("Event already exists.");
                } else {
                    tvErrorMessage.setText("Error creating event.");
                }
            }


            @Override
            public void onFailure(Call<CreateEventModel> call, Throwable t) {
                tvErrorMessage.setText("Network error: " + t.getMessage());
                Toast.makeText(requireContext(), "Failed to create event: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("CreateEvent", "Error: ", t);
            }


        });
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // TODO: Implement this method based on your file handling logic.
        // For example: obtain InputStream, create RequestBody, and then MultipartBody.Part.
        // Below is a pseudocode example — adjust it to match your implementation.

        // Pseudocode:
        // File file = FileUtils.getFileFromUri(getContext(), fileUri);
        // RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        // return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);

        return null; // TODO: Replace with actual implementation
    }

    private void clearForm() {
        etName.setText("");
        etDescription.setText("");
        etGuestNumber.setText("");
        etLocation.setText("");
        etStartDate.setText("");
        etEndDate.setText("");
        spinnerEventType.setSelection(0);
        selectedCategories.clear();
        layoutCategories.removeAllViews();
        btnUploadPhoto.setText("Upload Photo");
        selectedImageUri = null;
    }
}
