package com.example.eventplanner.ui.fragment.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.data.model.solutions.Category;
import com.example.eventplanner.databinding.FragmentAddBudgetItemDialogBinding;


public class AddBudgetItemDialogFragment extends DialogFragment {
    private FragmentAddBudgetItemDialogBinding binding;
    private EventBudgetViewModel viewModel;



    public static AddBudgetItemDialogFragment newInstance() {
        return new AddBudgetItemDialogFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBudgetItemDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EventBudgetViewModel.class);

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categories
            );

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.categorySpinner.setAdapter(adapter);
        });

        binding.cancelButton.setOnClickListener(v -> dismiss());
        binding.addButton.setOnClickListener(v -> {
            Category category = (Category) binding.categorySpinner.getSelectedItem();
            if (category == null) {
                ((TextView) binding.categorySpinner.getSelectedView()).setError("Category is required");
                return;
            }

            String amountStr = binding.amountInput.getText().toString().trim();
            if (amountStr.isBlank()) {
                binding.amountInput.setError("Amount is required");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                binding.amountInput.setError("Invalid amount");
                return;
            }

            if (amount < 0) {
                binding.amountInput.setError("Amount must be non-negative, not " + amount + ".");
                return;
            }

            viewModel.addBudgetItem(category.getId(), amount);
            dismiss();
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

}