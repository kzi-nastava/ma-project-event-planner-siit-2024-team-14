package com.example.eventplanner.ui.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.databinding.FragmentCategoryManagementBinding;
import com.example.eventplanner.ui.adapter.CategoryListAdapter;
import com.example.eventplanner.ui.viewmodel.CategoriesViewModel;

import java.util.ArrayList;

public class CategoryManagementFragment extends Fragment {

    private FragmentCategoryManagementBinding binding;


    public CategoryManagementFragment() { }


    public static CategoryManagementFragment newInstance() {
        CategoryManagementFragment fragment = new CategoryManagementFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentCategoryManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeCategories();
    }


    private CategoriesViewModel categoriesViewModel;

    private void initializeCategories() {
        categoriesViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);

        ArrayAdapter<Category> categoryListAdapter = new CategoryListAdapter(requireContext(), new ArrayList<>());
        binding.lwCategories.setAdapter(categoryListAdapter);

        categoriesViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryListAdapter.clear();
            categoryListAdapter.addAll(categories);
        });

        categoriesViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        );

        categoriesViewModel.loadCategories();
    }

    @Override
    public void onResume() {
        super.onResume();

        categoriesViewModel.loadCategories();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }
}