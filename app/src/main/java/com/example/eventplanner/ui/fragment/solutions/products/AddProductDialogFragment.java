package com.example.eventplanner.ui.fragment.solutions.products;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.products.CreateProductRequest;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.services.solutions.CategoryService;
import com.example.eventplanner.data.network.services.event_type.EventTypeService; // create if not present
import com.example.eventplanner.data.network.services.solutions.ProductService; // <-- add to ClientUtils
import com.example.eventplanner.data.model.solutions.Category;
import com.example.eventplanner.data.model.events.EventType;
import com.example.eventplanner.data.model.solutions.products.ProductModel; // response type if you have it

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** A bottom-sheet dialog that mirrors the web AddProduct form. */
public class AddProductDialogFragment extends BottomSheetDialogFragment {

    // Services (wire these in ClientUtils)
    private final CategoryService categoryService = ClientUtils.categoryService;
    private final EventTypeService eventTypeService = ClientUtils.eventTypeService;
    private final ProductService productService = ClientUtils.productService;

    // Views
    private TextInputEditText etName, etDescription, etPrice, etDiscount, etStatus, etNewCatName, etNewCatDesc;
    private AutoCompleteTextView actvCategory;
    private MaterialCheckBox cbNewCategory;
    private LinearLayout newCategoryGroup;
    private ChipGroup chipGroupEventTypes;

    // Data
    private List<Category> categories = new ArrayList<>();
    private List<EventType> eventTypes = new ArrayList<>();
    private Category selectedCategory = null;

    public static AddProductDialogFragment newInstance() {
        return new AddProductDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Optional: custom theme
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);

        etName = v.findViewById(R.id.etName);
        etDescription = v.findViewById(R.id.etDescription);
        etPrice = v.findViewById(R.id.etPrice);
        etDiscount = v.findViewById(R.id.etDiscount);
        etStatus = v.findViewById(R.id.etStatus);
        etNewCatName = v.findViewById(R.id.etNewCategoryName);
        etNewCatDesc = v.findViewById(R.id.etNewCategoryDesc);

        actvCategory = v.findViewById(R.id.actvCategory);
        cbNewCategory = v.findViewById(R.id.cbNewCategory);
        newCategoryGroup = v.findViewById(R.id.newCategoryGroup);
        chipGroupEventTypes = v.findViewById(R.id.chipGroupEventTypes);

        MaterialButton btnCreate = v.findViewById(R.id.btnCreate);
        MaterialButton btnCancel = v.findViewById(R.id.btnCancel);
        ImageButton btnClose = v.findViewById(R.id.btnClose);

        cbNewCategory.setOnCheckedChangeListener((buttonView, isChecked) ->
                newCategoryGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );

        btnCancel.setOnClickListener(view -> dismiss());
        btnClose.setOnClickListener(view -> dismiss());
        btnCreate.setOnClickListener(view -> submit());

        fetchCategories();
        fetchEventTypes();

        return v;
    }

    private void fetchCategories() {
        categoryService.getAll().enqueue(new Callback<List<Category>>() {
            @Override public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            mapCategoryNames(categories)
                    );
                    actvCategory.setAdapter(adapter);
                    actvCategory.setOnItemClickListener((parent, view, position, id) -> {
                        String name = (String) parent.getItemAtPosition(position);
                        selectedCategory = findCategoryByName(name);
                    });
                } else {
                    toast("Failed to load categories: " + response.code());
                }
            }
            @Override public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                toast("Failed to load categories: " + t.getMessage());
            }
        });
    }

    private void fetchEventTypes() {
        eventTypeService.getAll().enqueue(new Callback<List<EventType>>() {
            @Override public void onResponse(@NonNull Call<List<EventType>> call, @NonNull Response<List<EventType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventTypes = response.body();
                    populateEventTypeChips();
                } else {
                    toast("Failed to load event types: " + response.code());
                }
            }
            @Override public void onFailure(@NonNull Call<List<EventType>> call, @NonNull Throwable t) {
                toast("Failed to load event types: " + t.getMessage());
            }
        });
    }

    private void populateEventTypeChips() {
        chipGroupEventTypes.removeAllViews();
        for (EventType t : eventTypes) {
            Chip chip = new Chip(requireContext());
            chip.setText(t.getName());
            chip.setCheckable(true);
            chip.setTag(t.getId());
            chipGroupEventTypes.addView(chip);
        }
    }

    private void submit() {
        String name = textOrEmpty(etName);
        if (TextUtils.isEmpty(name) || name.length() < 3) {
            toast("Name must be at least 3 characters");
            return;
        }

        Double price = parseDoubleOrNull(textOrEmpty(etPrice));
        if (price == null || price < 0) {
            toast("Price must be a positive number");
            return;
        }

        Integer discount = parseIntOrZero(textOrEmpty(etDiscount));
        if (discount < 0 || discount > 100) {
            toast("Discount must be 0-100");
            return;
        }

        // Collect event type ids from checked chips
        List<Long> eventTypeIds = new ArrayList<>();
        for (int i = 0; i < chipGroupEventTypes.getChildCount(); i++) {
            View child = chipGroupEventTypes.getChildAt(i);
            if (child instanceof Chip) {
                Chip c = (Chip) child;
                if (c.isChecked() && c.getTag() instanceof Number) {
                    eventTypeIds.add(((Number) c.getTag()).longValue());
                }
            }
        }

        // Build request payload mirroring your web component
        CreateProductRequest req = new CreateProductRequest();
        req.name = name;
        req.description = textOrEmpty(etDescription);
        req.price = price;
        req.discount = discount.doubleValue();
        req.visible = true;    // could add a switch if desired
        req.available = true;  // could add a switch if desired
        req.status = TextUtils.isEmpty(textOrEmpty(etStatus)) ? "PENDING" : textOrEmpty(etStatus);
        req.applicableEventTypeIds = eventTypeIds;

        if (cbNewCategory.isChecked()) {
            // propose new category
            req.category = new CreateProductRequest.CategoryRef();
            req.category.name = textOrEmpty(etNewCatName);
            req.category.description = textOrEmpty(etNewCatDesc);
            if (TextUtils.isEmpty(req.category.name)) {
                toast("New category name is required");
                return;
            }
        } else {
            if (selectedCategory == null) {
                toast("Please choose a category");
                return;
            }
            req.category = new CreateProductRequest.CategoryRef();

            Integer catId = (selectedCategory != null) ? selectedCategory.getId() : null;
            if (catId != null && catId >= 0) {           // existing category chosen
                req.category.id = catId.longValue();     // <-- convert Integer -> Long
                req.category.name = null;                // don't send name/desc when using existing id
                req.category.description = null;
            } else {                                     // "New Category" path
                req.category.id = null;                  // let backend create id
                // req.category.name / description should already be set from the form
            }
        }

        productService.create(req).enqueue(new Callback<ProductModel>() {
            @Override public void onResponse(@NonNull Call<ProductModel> call, @NonNull Response<ProductModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (cbNewCategory.isChecked()) {
                        toast("Proposed new category \"" + req.category.name + "\". Admin will review.");
                    } else {
                        toast("Created product: \"" + response.body().getName() + "\"");
                    }
                    dismiss();
                } else {
                    toast("Failed to create product: " + response.code());
                }
            }
            @Override public void onFailure(@NonNull Call<ProductModel> call, @NonNull Throwable t) {
                toast("Failed to create product: " + t.getMessage());
            }
        });
    }

    // Helpers
    private List<String> mapCategoryNames(List<Category> cats) {
        List<String> names = new ArrayList<>();
        for (Category c : cats) names.add(c.getName());
        return names;
    }
    private Category findCategoryByName(String name) {
        for (Category c : categories) if (TextUtils.equals(c.getName(), name)) return c;
        return null;
    }
    private String textOrEmpty(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
    private Double parseDoubleOrNull(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return null; }
    }
    private Integer parseIntOrZero(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }
    private void toast(String m) { Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show(); }
}
