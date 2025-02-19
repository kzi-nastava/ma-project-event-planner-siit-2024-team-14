package com.example.eventplanner.ui.fragment.category;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.NoCopySpan;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.databinding.FragmentNewCategoryBinding;

import org.w3c.dom.Text;

import java.util.Objects;
import java.util.Optional;

public class NewCategoryFragment extends Fragment {

    private NewCategoryViewModel viewModel;
    private FragmentNewCategoryBinding binding;

    private static final String ARG_ID = "id", ARG_NAME = "name", ARG_DESCRIPTION = "desc";


    public static NewCategoryFragment newInstance() {
        return new NewCategoryFragment();
    }

    public static NewCategoryFragment newInstance(@NonNull Category category) {
        NewCategoryFragment fragment = new NewCategoryFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_ID, category.getId());
        args.putString(ARG_NAME, category.getName());
        args.putString(ARG_DESCRIPTION, category.getDescription());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentNewCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NewCategoryViewModel.class);

        Bundle args = getArguments();
        Category category = new Category();
        // different form title and btn text when updating/creating
        int titleResId = R.string.title_create_category;
        int submitTextResId = R.string.create;

        if (args != null) {
            int id = args.getInt(ARG_ID, -1);
            if (id != -1) {
                // if id is passed then we are updating an existent category
                category.setId(id);
                titleResId = R.string.title_update_category;
                submitTextResId = R.string.update;
            }

            category.setName(args.getString(ARG_NAME, ""));
            category.setDescription(args.getString(ARG_DESCRIPTION, ""));
        }

        binding.twTitle.setText(titleResId);
        binding.btnSubmit.setText(submitTextResId);

        viewModel.setCategory(category);
        binding.etCategoryName.setText(category.getName());
        binding.etCategoryDescription.setText(category.getDescription());

        binding.etCategoryName.addTextChangedListener(
                (SimpleTextWatcher) (charSequence, i, i1, i2) -> viewModel.setCategoryName(charSequence.toString())
        );

        binding.etCategoryDescription.addTextChangedListener(
                (SimpleTextWatcher) (charSequence, i, i1, i2) -> viewModel.setCategoryDescription(charSequence.toString())
        );

        binding.btnSubmit.setOnClickListener(v -> onSubmit());

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        viewModel.getSuccess().observe(lifecycleOwner, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Successfully added a new category", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        viewModel.getErrorMsg().observe(lifecycleOwner, msg -> {
            if (msg != null && !msg.isBlank()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onSubmit() {
        Category category = viewModel.getCategory().getValue();

        // validate input data
        if (category == null) { return; }

        if (category.getName() == null || category.getName().isBlank()) {
            Toast.makeText(requireContext(), "Invalid category name", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.submitCategory();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    // TODO: Figure out the appropriate place for this interface?

    interface SimpleTextWatcher extends TextWatcher {
        @Override
        default void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        default void afterTextChanged(Editable editable) {}
    }
}