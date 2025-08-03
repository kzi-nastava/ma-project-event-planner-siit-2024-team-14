package com.example.eventplanner.ui.fragment.solutions.services;

import static com.example.eventplanner.ui.util.SimpleTextWatcher.AfterTextChanged;
import static com.example.eventplanner.ui.util.Util.*;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.Category;
import com.example.eventplanner.data.model.solutions.services.CreateService;
import com.example.eventplanner.data.model.solutions.services.ReservationType;
import com.example.eventplanner.data.model.solutions.services.ServiceModel;
import com.example.eventplanner.data.model.users.UserModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.databinding.FragmentAddServiceBinding;
import com.example.eventplanner.databinding.ViewServiceFormBinding;
import com.example.eventplanner.ui.fragment.FragmentTransition;
import com.example.eventplanner.ui.fragment.HomeFragment;
import com.example.eventplanner.ui.fragment.solutions.SolutionDetailsFragment;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class AddServiceFragment extends Fragment {
    private ViewServiceFormBinding form;
    AddServiceViewModel viewModel;

    /// `Category::id` -> position in the category select
    private Map<Integer, Integer> categoryOptions = Map.of();



    public static AddServiceFragment newInstance() {
        return new AddServiceFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentAddServiceBinding binding = FragmentAddServiceBinding.inflate(inflater, container, false);

        form = binding.form;
        form.deleteButton.setVisibility(View.GONE);
        form.submitButton.setText(R.string.create);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!ClientUtils.authService.hasRole(UserModel.ROLE_PROVIDER))
            view.post(() ->
                    FragmentTransition.to(new HomeFragment(), requireActivity(), R.id.home_page_fragment)
            );

        viewModel = new ViewModelProvider(this).get(AddServiceViewModel.class);

        viewModel.service().observe(getViewLifecycleOwner(), this::populateServiceData);
        viewModel.createdService().observe(getViewLifecycleOwner(), this::onServiceCreated);
        viewModel.categories().observe(getViewLifecycleOwner(), this::setupCategorySelect);
        viewModel.error().observe(getViewLifecycleOwner(), this::toastError);

        setupCategorySelect(List.of()); // don't wait for categories to load to show new category option
        setupInputListeners();
    }


    private void populateServiceData(CreateService service) {
        if (service == null)
            return;

        Optional.ofNullable(service.getName())
                        .ifPresent(form.nameInput::setText);

        Optional.ofNullable(service.getDescription())
                .ifPresent(form.descriptionInput::setText);

        Optional.ofNullable(service.getPrice())
                .map(String::valueOf)
                .ifPresent(form.priceInput::setText);

        Optional.ofNullable(service.getDiscount())
                .map(String::valueOf)
                .ifPresent(form.discountInput::setText);

        Optional.ofNullable(service.getDurationMinutes())
                .map(String::valueOf)
                .ifPresent(form.durationInput::setText);

        Optional.ofNullable(service.getReservationPeriodDays())
                .map(String::valueOf)
                .ifPresent(form.reservationPeriodInput::setText);

        Optional.ofNullable(service.getCancellationPeriodDays())
                .map(String::valueOf)
                .ifPresent(form.cancellationPeriodInput::setText);

        Optional.ofNullable(service.getReservationType())
                .map(ReservationType.AUTOMATIC::equals)
                .ifPresent(form.autoAcceptCbx::setChecked);

        Category category = service.getCategory();
        if (category == null)
            return;

        selectCategory(category.getId());

        Optional.ofNullable(category.getName())
                .ifPresent(form.categoryNameInput::setText);

        Optional.ofNullable(category.getDescription())
                .ifPresent(form.categoryDescriptionInput::setText);
    }


    private void setupCategorySelect(List<Category> categories) {
        categoryOptions = categories.stream()
                .collect(Collectors.toMap(Category::getId, categories::indexOf));

        List<String> options = categories.stream().map(Category::getName).collect(Collectors.toList());
        options.add(getString(R.string.new_category_option));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                options
        );
        form.categorySpinner.setAdapter(adapter);
    }



    private void selectCategory(Integer categoryId) {
        // select new category if null else, existent category by id (fallback to invalid position)
        @SuppressWarnings("ConstantConditions") // the map does not have null values (position)
        int position = categoryId == null ? categoryOptions.size() : categoryOptions.getOrDefault(categoryId, Spinner.INVALID_POSITION);

        try {
            form.categorySpinner.setSelection(position);
        } catch (Exception e) {  // couldn't find anything about what happens if invalid position
            // pass
        }
    }


    private void setupInputListeners() {
        form.nameInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    String name = editable.toString().trim();
                    viewModel.updateService(CreateService::setName, name);
                })
        );

        form.descriptionInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    String description = editable.toString().trim();
                    viewModel.updateService(CreateService::setDescription, description);
                })
        );

        form.priceInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    double price = parseDouble(editable.toString().trim(), -1);
                    viewModel.updateService(CreateService::setPrice, price);
                })
        );

        form.discountInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    double discount = parseDouble(editable.toString().trim(), 0);
                    viewModel.updateService(CreateService::setDiscount, discount);
                })
        );

        form.categoryNameInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    String name = editable.toString().trim();

                    viewModel.updateService(
                            (service, value) -> {
                                Category category;

                                if ((category = service.getCategory()) == null)
                                    service.setCategory(category = new Category());

                                category.setName(value);
                            },
                            name
                    );
                })
        );

        form.categoryDescriptionInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    String description = editable.toString().trim();

                    viewModel.updateService(
                            (service, value) -> {
                                Category category;

                                if ((category = service.getCategory()) == null)
                                    service.setCategory(category = new Category());

                                category.setDescription(value);
                            },
                            description
                    );
                })
        );

        form.durationInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    long durationMinutes = parseInt(editable.toString().trim(), -1);
                    viewModel.updateService(CreateService::setDurationMinutes, durationMinutes);
                })
        );

        form.reservationPeriodInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    long reservationPeriodDays = parseInt(editable.toString().trim(), 0);
                    viewModel.updateService(CreateService::setReservationPeriodDays, reservationPeriodDays);
                })
        );

        form.cancellationPeriodInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    long cancellationPeriodDays = parseInt(editable.toString().trim(), 0);
                    viewModel.updateService(CreateService::setCancellationPeriodDays, cancellationPeriodDays);
                })
        );

        form.autoAcceptCbx.setOnCheckedChangeListener((buttonView, isChecked) ->
                viewModel.updateService(CreateService::setReservationType, isChecked ? ReservationType.AUTOMATIC : ReservationType.MANUAL)
        );

        form.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                boolean isAddingNew = position == categoryOptions.size();
                form.newCategoryContainer.setVisibility(isAddingNew ? View.VISIBLE : View.GONE);

                if (isAddingNew) {
                    String name = Optional.ofNullable(form.categoryNameInput.getText()).map(Object::toString).map(String::trim).orElse(null);
                    String description = Optional.ofNullable(form.categoryDescriptionInput.getText()).map(Object::toString).map(String::trim).orElse(null);

                    viewModel.updateService(CreateService::setCategory, new Category(null, name, description));
                } else {
                    categoryOptions.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(position))
                            .findFirst()
                            .map(Map.Entry::getKey)
                            .ifPresent(
                                    categoryId -> viewModel.updateService(CreateService::setCategory, new Category(categoryId))
                            );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        form.submitButton.setOnClickListener(v -> viewModel.createService());
    }


    private void onServiceCreated(ServiceModel createdService) {
        Toast.makeText(requireContext(), R.string.created_service_message, Toast.LENGTH_SHORT).show();
        // Go to service details
        FragmentTransition.to(
                SolutionDetailsFragment.newInstance(createdService),
                requireActivity(),
                R.id.home_page_fragment
        );
    }


    private void toastError(String err) {
        if (err != null)
            Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
    }

}