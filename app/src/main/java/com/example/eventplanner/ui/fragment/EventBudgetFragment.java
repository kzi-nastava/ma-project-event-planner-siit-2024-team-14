package com.example.eventplanner.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.databinding.FragmentEventBudgetBinding;
import com.example.eventplanner.ui.adapter.BudgetItemAdapter;

import java.text.Format;
import java.text.NumberFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventBudgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventBudgetFragment extends Fragment {
    private static final String ARG_EVENT_ID = "id";
    private static final Format NUMBER_FMT = NumberFormat.getCurrencyInstance();

    private int eventId;
    private FragmentEventBudgetBinding binding;
    private EventBudgetViewModel viewModel;


    public EventBudgetFragment() {
        // Required empty public constructor
    }


    public static EventBudgetFragment newInstance(int eventId) {
        EventBudgetFragment fragment = new EventBudgetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getInt(ARG_EVENT_ID);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EventBudgetViewModel.class);

        viewModel.error.observe(getViewLifecycleOwner(), err -> {
            if (err != null)
                Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });

        final BudgetItemAdapter adapter = new BudgetItemAdapter(List.of());
        binding.budgetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.budgetRecyclerView.setAdapter(adapter);

        viewModel.budget.observe(getViewLifecycleOwner(), budget -> {
            binding.totalPlanned.setText(NUMBER_FMT.format(budget.getAmount()));
            binding.totalSpent.setText(NUMBER_FMT.format(budget.getSpent()));
            adapter.setItems(budget.getItems());
        });

        binding.addButton.setOnClickListener(v ->
            AddBudgetItemDialogFragment.newInstance()
                    .show(getParentFragmentManager(), "AddBudgetItemDialog")
        );

        viewModel.fetchBudget(eventId);
        viewModel.fetchCategories();
    }


    @Override
    public void onResume() {
        super.onResume();

        viewModel.fetchBudget(eventId);
        viewModel.fetchCategories();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}