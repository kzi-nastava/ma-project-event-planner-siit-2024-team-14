package com.example.eventplanner.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.EventTypeModel;
import com.example.eventplanner.data.model.ProductModel;
import com.example.eventplanner.data.model.ProviderModel;
import com.example.eventplanner.data.network.ApiClient;
import com.example.eventplanner.data.network.services.profiles.ProviderService;
import com.example.eventplanner.data.network.services.solutions.ProductService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsFragment extends Fragment {

    private TextView nameTextView, descriptionTextView, categoryTextView, eventTypesTextView,
            priceTextView, discountedPriceTextView, providerTextView;
    private Button chatButton, purchaseButton, editButton;

    private int loggedUserId;
    private String userRole;
    private int productId;
    private ProductModel product;

    public static ProductDetailsFragment newInstance(int productId) {
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("productId", productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        nameTextView = view.findViewById(R.id.nameTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);
        categoryTextView = view.findViewById(R.id.categoryTextView);
        eventTypesTextView = view.findViewById(R.id.eventTypesTextView);
        priceTextView = view.findViewById(R.id.priceTextView);
        discountedPriceTextView = view.findViewById(R.id.discountedPriceTextView);
        providerTextView = view.findViewById(R.id.providerTextView);

        chatButton = view.findViewById(R.id.chatButton);
        purchaseButton = view.findViewById(R.id.purchaseButton);
        editButton = view.findViewById(R.id.editButton);

        if (getArguments() != null) {
            productId = getArguments().getInt("productId", -1);
        }

        if (productId == -1) {
            Toast.makeText(getContext(), "Invalid product ID", Toast.LENGTH_SHORT).show();
            Log.e("ProductDetails", "Invalid product ID");
            return view;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        loggedUserId = prefs.getInt("userId", -1);
        userRole = prefs.getString("role", null);

        loadProductDetails(productId);

        return view;
    }

    private void loadProductDetails(int id) {
        ProductService api = ApiClient.getClient().create(ProductService.class);
        Call<ProductModel> call = api.getProductById(id);

        call.enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(@NonNull Call<ProductModel> call, @NonNull Response<ProductModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body();
                    populateUI();
                } else {
                    Toast.makeText(getContext(), "Failed to load product details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductModel> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to load product.", Toast.LENGTH_SHORT).show();
                Log.e("ProductDetails", "API call failed", t);
            }
        });
    }

    private void populateUI() {
        if (product == null) {
            Toast.makeText(getContext(), "No product data available", Toast.LENGTH_SHORT).show();
            return;
        }

        nameTextView.setText(product.getName() != null ? product.getName() : "N/A");
        descriptionTextView.setText(product.getDescription() != null ? product.getDescription() : "N/A");

        if (product.getCategory() != null) {
            categoryTextView.setText(product.getCategory().getName() + ": " + product.getCategory().getDescription());
        } else {
            categoryTextView.setText("Category not available");
        }

        // Event types
        StringBuilder eventTypes = new StringBuilder();
        List<EventTypeModel> types = product.getApplicableEventTypes();
        if (types != null && !types.isEmpty()) {
            for (EventTypeModel type : types) {
                eventTypes.append("• ").append(type.getName()).append("\n");
            }
        }
        eventTypesTextView.setText(eventTypes.toString());

        // Price & discount
        if (product.getDiscount() > 0) {
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            priceTextView.setText(String.format("%.2f RSD", product.getPrice()));
            double discounted = product.getPrice() * (1 - product.getDiscount());
            discountedPriceTextView.setText(String.format("%.2f RSD (%.0f%% off)", discounted, product.getDiscount() * 100));
            discountedPriceTextView.setVisibility(View.VISIBLE);
        } else {
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            priceTextView.setText(String.format("%.2f RSD", product.getPrice()));
            discountedPriceTextView.setVisibility(View.GONE);
        }

        // Ovde pozivamo API da dobijemo providera po ID-ju
        fetchProvider(product.getProviderId());

        // Buttons visibility and listeners
        if ("EventOrganizer".equals(userRole)) {
            chatButton.setVisibility(View.VISIBLE);
            purchaseButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);

            chatButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Chat feature not implemented yet.", Toast.LENGTH_SHORT).show();
            });

            purchaseButton.setOnClickListener(v -> {
                showPurchasePopup(product.getId());
            });

        } else if ("ServiceAndProductProvider".equals(userRole) && product.getProviderId() == loggedUserId) {
            chatButton.setVisibility(View.GONE);
            purchaseButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);

            editButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Edit feature not implemented yet.", Toast.LENGTH_SHORT).show();
            });
        } else {
            chatButton.setVisibility(View.GONE);
            purchaseButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
        }
    }

    private void fetchProvider(int providerId) {
        ProviderService apiService = ApiClient.getProviderService();
        Call<ProviderModel> call = apiService.getProviderById(providerId);

        call.enqueue(new Callback<ProviderModel>() {
            @Override
            public void onResponse(Call<ProviderModel> call, Response<ProviderModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProviderModel provider = response.body();
                    String fullName = "Provided by: " + (provider.getCompanyName() != null ? provider.getCompanyName() : provider.getEmail());

                    if (providerId == loggedUserId) {
                        providerTextView.setText(fullName);
                        providerTextView.setOnClickListener(null);
                    } else {
                        providerTextView.setText(Html.fromHtml("<u>" + fullName + "</u>"));
                        providerTextView.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putInt("providerId", provider.getId());

                            ViewProviderProfileFragment fragment = new ViewProviderProfileFragment();
                            fragment.setArguments(bundle);

                            requireActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.home_page_fragment, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        });
                    }
                } else {
                    providerTextView.setText("Provider info unavailable");
                }
            }

            @Override
            public void onFailure(Call<ProviderModel> call, Throwable t) {
                providerTextView.setText("Provider info unavailable");
            }
        });
    }

    private void showPurchasePopup(int productId) {
        // TODO: Implement purchase popup dialog
        Toast.makeText(getContext(), "Purchase popup not implemented yet.", Toast.LENGTH_SHORT).show();
    }
}
