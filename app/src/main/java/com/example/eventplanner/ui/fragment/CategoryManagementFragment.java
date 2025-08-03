package com.example.eventplanner.ui.fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.databinding.FragmentCategoryManagementBinding;
import com.example.eventplanner.ui.adapter.CategoryListAdapter;
import com.example.eventplanner.ui.fragment.category.NewCategoryFragment;
import com.example.eventplanner.ui.viewmodel.CategoriesViewModel;

public class CategoryManagementFragment extends Fragment {

    private FragmentCategoryManagementBinding binding;
    private CategoriesViewModel viewModel;
    private CategoryListAdapter adapter;


    public CategoryManagementFragment() { }


    public static CategoryManagementFragment newInstance() {
        return new CategoryManagementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentCategoryManagementBinding.inflate(inflater, container, false);

        binding.lwCategories.setAdapter(adapter = new CategoryListAdapter(requireContext()));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);

        viewModel.getCategories().observe(getViewLifecycleOwner(), adapter::addAll);
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), err -> {
            if (err != null)
                Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });

        binding.actionAddCategory.setOnClickListener(v ->
                requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_page_fragment, NewCategoryFragment.newInstance())
                .addToBackStack(null)
                .commit()
        );

        adapter.setOnCategoryClickedListener(new CategoryListAdapter.OnCategoryClickedListener() {
            @Override
            public void onCategoryDelete(Category category) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete \"" + category.getName() + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            viewModel.getDeleted().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                                @Override
                                public void onChanged(Integer id) {
                                    if (id != null && id.equals(category.getId())) {
                                        Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show();
                                        viewModel.getDeleted().removeObserver(this);
                                        viewModel.loadCategories();
                                    }
                                }
                            });
                            viewModel.deleteCategory(category);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onCategoryUpdate(Category category) {
                Fragment updateCategoryFragment = NewCategoryFragment.newInstance(category);

                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_page_fragment, updateCategoryFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        viewModel.loadCategories();
    }


    @Override
    public void onResume() {
        super.onResume();

        viewModel.loadCategories();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }
}