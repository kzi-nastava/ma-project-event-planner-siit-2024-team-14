package com.example.eventplanner.ui.fragment.solutions.products;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.data.model.Category;
import com.example.eventplanner.data.model.UserDTO;
import com.example.eventplanner.data.model.solutions.products.Product;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.databinding.FragmentProductDetailsBinding;

import java.util.Optional;


public class ProductDetailsFragment extends Fragment {
    private static final String ARG_ID = "id";

    private int id;

    private FragmentProductDetailsBinding binding;
    private ProductViewModel viewModel;


    public ProductDetailsFragment() { }

    public static ProductDetailsFragment newInstance(int id) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_ID);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);

        Optional.ofNullable(ClientUtils.authService.getUser()) // maybe extract actions to separate fragment to avoid duplication on service/product details
                .map(UserDTO::getRole)
                .ifPresent(role -> {
                    if (UserDTO.ROLE_ORGANIZER.equalsIgnoreCase(role)) {
                        binding.purchaseProductButton.setVisibility(View.VISIBLE);
                        binding.purchaseProductButton.setOnClickListener(v -> {
                            Log.i(getClass().getSimpleName(), "TODO: Implement product purchasing");
                        });
                        binding.chatWithProviderButton.setVisibility(View.VISIBLE);
                    } else if (UserDTO.ROLE_PROVIDER.equalsIgnoreCase(role)) {
                        binding.editProductButton.setVisibility(View.VISIBLE);
                    }
                });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        viewModel.errorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null)
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });
        viewModel.product().observe(getViewLifecycleOwner(), this::showProductDetails);

        viewModel.fetchProduct(id);
    }


    private void showProductDetails(Product product) {
        binding.productName.setText(product.getName());
        binding.productDescription.setText(product.getDescription());
        binding.categoryName.setText(Optional.ofNullable(product.getCategory()).map(Category::getName).orElse(""));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
