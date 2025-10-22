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

public class EditEventTypeDialog extends Dialog {

    public interface OnEventEditedListener {
        void onEdited(EventType updatedEvent);
        void onDeleted(EventType event);
    }

    private EventType eventData;
    private OnEventEditedListener listener;

    private EditText etName, etDescription;
    private LinearLayout layoutCategories;
    private TextView tvError;
    private Button btnSave, btnDelete;

    private List<String> selectedCategories = new ArrayList<>();
    private List<CategoriesEtModel> categories = new ArrayList<>();

    public EditEventTypeDialog(@NonNull Context context, EventType eventData, OnEventEditedListener listener) {
        super(context);
        this.eventData = eventData;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_event_type);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        layoutCategories = findViewById(R.id.layoutCategories);
        tvError = findViewById(R.id.tvError);
        btnSave = findViewById(R.id.btnSave);

        etName.setText(eventData.getName());
        etDescription.setText(eventData.getDescription());
        if (eventData.getCategories() != null) {
            for (CategoriesEtModel c : eventData.getCategories()) {
                selectedCategories.add(c.getName());
            }
        }

        loadCategories();

        btnSave.setOnClickListener(v -> saveEvent());

    }

    private void loadCategories() {
        ClientUtils.eventTypeService.getAllCategories().enqueue(new Callback<List<CategoriesEtModel>>() {
            @Override
            public void onResponse(Call<List<CategoriesEtModel>> call, Response<List<CategoriesEtModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories.clear();
                    categories.addAll(response.body());
                    layoutCategories.removeAllViews();

                    for (CategoriesEtModel cat : categories) {
                        CheckBox checkBox = new CheckBox(getContext());
                        checkBox.setText(cat.getName());
                        checkBox.setTextColor(0xFF523D35);
                        checkBox.setChecked(selectedCategories.contains(cat.getName()));

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
                tvError.setText("Network error");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveEvent() {
        String desc = etDescription.getText().toString().trim();
        if (desc.isEmpty() || selectedCategories.isEmpty()) {
            tvError.setText("Please fill description and select at least one category");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        EventType updatedEvent = new EventType(
                eventData.getId(),
                eventData.getName(),
                desc,
                eventData.isActive(),
                mapSelectedCategories()
        );

        ClientUtils.eventTypeService.updateEventType(updatedEvent).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onEdited(updatedEvent);
                    dismiss();
                } else {
                    tvError.setText("Error updating event");
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                tvError.setText("Network error");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private List<CategoriesEtModel> mapSelectedCategories() {
        List<CategoriesEtModel> list = new ArrayList<>();
        for (String s : selectedCategories) {
            CategoriesEtModel c = new CategoriesEtModel();
            c.setName(s);
            list.add(c);
        }
        return list;
    }

}

