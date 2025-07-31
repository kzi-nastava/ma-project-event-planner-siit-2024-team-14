package com.example.eventplanner.ui.fragment.solutions;

import static android.graphics.Color.TRANSPARENT;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.eventplanner.data.model.solutions.Category;
import com.example.eventplanner.data.model.solutions.FilterParams;
import com.example.eventplanner.databinding.FragmentSolutionFiltersDialogBinding;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SolutionFiltersDialogFragment extends DialogFragment {
    private static final String ARG_FILTERS = "filters";

    private FragmentSolutionFiltersDialogBinding binding;
    private FilterParams filters;

    private OnFilterActionListener listener;

    public void setOnFilterListener(OnFilterActionListener listener) {
        this.listener = listener;
    }

    public interface OnFilterActionListener {
        void onApplyFilters(FilterParams filters);
        default void onCancel() {}
    }



    public static SolutionFiltersDialogFragment newInstance() {
        return newInstance(new FilterParams());
    }

    public static SolutionFiltersDialogFragment newInstance(FilterParams filters) {
        SolutionFiltersDialogFragment fragment = new SolutionFiltersDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILTERS, filters);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    @SuppressWarnings("nullpointer")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            filters = (FilterParams) savedInstanceState.getSerializable(ARG_FILTERS);
            return;
        } catch (NullPointerException | ClassCastException e) {
            // pass
        }

        try {
            filters = (FilterParams) requireArguments().get(ARG_FILTERS);
            return;
        } catch (NullPointerException | ClassCastException e) {
            // pass
        }

        filters = new FilterParams();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSolutionFiltersDialogBinding.inflate(inflater, container, false);

        binding.buttonCancel.setOnClickListener(v -> {
            Optional.ofNullable(listener)
                    .ifPresent(OnFilterActionListener::onCancel);

            dismiss();
        });

        binding.buttonApply.setOnClickListener(v -> {
            Optional.ofNullable(listener)
                            .ifPresent(listener -> listener.onApplyFilters(filters));

            dismiss();
        });

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SolutionsViewModel categoriesViewModel = new ViewModelProvider(this).get(SolutionsViewModel.class);

        categoriesViewModel.error.observe(getViewLifecycleOwner(), this::showError);
        categoriesViewModel.categories.observe(getViewLifecycleOwner(), this::setupCategorySelect);

        categoriesViewModel.fetchCategories();

        Optional.ofNullable(filters.getMaxPrice())
                .ifPresent(max -> binding.inputMaxPrice.setText(String.valueOf(max)));

        Optional.ofNullable(filters.getMinPrice())
                .ifPresent(min -> binding.inputMinPrice.setText(String.valueOf(min)));
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_FILTERS, filters);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void showError(String err) {
        if (err != null)
            Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
    }


    private void setupCategorySelect(List<Category> availableCategories) {
        List<String> items = Stream.concat(Stream.of("All"), availableCategories.stream().map(Category::getName))
                .collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.selectCategory.setAdapter(adapter);

        binding.selectCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // plan was to allow multiple categories, but that would require custom select
                filters.setCategory( i == 0 ? null : Set.of(availableCategories.get(i - 1).getId()) );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                filters.setCategory(null);
            }

        });

        // select the category if saved in filters
        Optional.ofNullable(filters.getCategory())
                .flatMap(set -> set.stream().findFirst())
                .map(categoryId -> {
                    for (int i = 0; i < availableCategories.size(); ++i)
                        if (Objects.equals(categoryId, availableCategories.get(i).getId()))
                            return i;

                    return -1;
                })
                .filter(i -> i >= 0)
                .ifPresent(i -> binding.selectCategory.setSelection(i + 1));
    }

}