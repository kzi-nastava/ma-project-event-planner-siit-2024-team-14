package com.example.eventplanner.ui.fragment.events.event_type_menagement;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.events.CategoriesEtModel;
import com.example.eventplanner.data.model.events.EventType;
import com.example.eventplanner.data.network.ClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventTypeDialog extends Dialog {

    private EditText etName, etDescription;
    private LinearLayout layoutCategories;
    private TextView tvError, tvSuccess;
    private Button btnCreate;
    private List<CategoriesEtModel> categories = new ArrayList<>();
    private List<String> selectedCategories = new ArrayList<>();

    public interface OnEventCreatedListener {
        void onCreated(EventType event);
    }

    private final OnEventCreatedListener listener;

    public AddEventTypeDialog(@NonNull Context context, OnEventCreatedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_event_type);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        layoutCategories = findViewById(R.id.layoutCategories);
        tvError = findViewById(R.id.tvError);
        tvSuccess = findViewById(R.id.tvSuccess);
        btnCreate = findViewById(R.id.btnCreate);

        loadCategories();

        btnCreate.setOnClickListener(v -> createEventType());
    }

    private void loadCategories() {
        ClientUtils.eventTypeService.getAllCategories().enqueue(new Callback<List<CategoriesEtModel>>() {
            @Override
            public void onResponse(Call<List<CategoriesEtModel>> call, Response<List<CategoriesEtModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    for (CategoriesEtModel cat : categories) {
                        CheckBox checkBox = new CheckBox(getContext());
                        checkBox.setText(cat.getName());
                        checkBox.setTextColor(0xFF523D35);
                        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if (isChecked) selectedCategories.add(cat.getName());
                            else selectedCategories.remove(cat.getName());
                        });
                        layoutCategories.addView(checkBox);
                    }
                } else {
                    tvError.setText("Failed to load categories");
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<CategoriesEtModel>> call, Throwable t) {
                tvError.setText("Error fetching categories");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createEventType() {
        tvError.setVisibility(View.GONE);
        tvSuccess.setVisibility(View.GONE);

        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || selectedCategories.isEmpty()) {
            tvError.setText("Please fill all fields and select at least one category");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        EventType newEvent = new EventType(null, name, desc, true, null);
        // map categories
        List<CategoriesEtModel> cats = new ArrayList<>();
        for (String s : selectedCategories) {
            CategoriesEtModel c = new CategoriesEtModel();
            c.setName(s);
            cats.add(c);
        }
        newEvent = new EventType(null, name, desc, true, cats);

        ClientUtils.eventTypeService.createEventType(newEvent).enqueue(new Callback<EventType>() {
            @Override
            public void onResponse(Call<EventType> call, Response<EventType> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvSuccess.setText("Event type created successfully!");
                    tvSuccess.setVisibility(View.VISIBLE);
                    listener.onCreated(response.body());
                    dismiss();
                } else {
                    tvError.setText("Error creating event type");
                    tvError.setVisibility(View.VISIBLE);
                }
            }



            @Override
            public void onFailure(Call<EventType> call, Throwable t) {
                tvError.setText("Network error creating event type");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }
}
