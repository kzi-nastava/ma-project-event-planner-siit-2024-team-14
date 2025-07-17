package com.example.eventplanner.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.eventplanner.data.model.ServiceModel;
import com.example.eventplanner.data.network.ApiClient;
import com.example.eventplanner.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailsFragment extends Fragment {

    private TextView nameTextView, descriptionTextView, priceTextView, durationTextView;
    private ProgressBar progressBar;
    private int serviceId;

    public static ServiceDetailsFragment newInstance(int solutionId) {
        ServiceDetailsFragment fragment = new ServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("solutionId", solutionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_service_details, container, false);

        Button bookServiceButton = view.findViewById(R.id.book_service_button);
        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String role = prefs.getString("role", null);

        if ("EventOrganizer".equals(role)) {
            bookServiceButton.setVisibility(View.VISIBLE);
            bookServiceButton.setOnClickListener(v -> {
                ServiceReservationFragment reservationFragment = ServiceReservationFragment.newInstance(serviceId);
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_page_fragment, reservationFragment)
                        .addToBackStack(null)
                        .commit();
            });
        } else {
            bookServiceButton.setVisibility(View.GONE);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        nameTextView = view.findViewById(R.id.service_name);
        descriptionTextView = view.findViewById(R.id.service_description);
        priceTextView = view.findViewById(R.id.service_price);
        durationTextView = view.findViewById(R.id.service_duration);
        progressBar = view.findViewById(R.id.loading_spinner);

        if (getArguments() != null) {
            serviceId = getArguments().getInt("solutionId", -1);
            if (serviceId != -1) {
                fetchServiceDetails(serviceId);
            }
        }
    }

    private void fetchServiceDetails(int id) {
        progressBar.setVisibility(View.VISIBLE);

        ApiClient.getServiceApi().getServiceById(id).enqueue(new Callback<ServiceModel>() {
            @Override
            public void onResponse(Call<ServiceModel> call, Response<ServiceModel> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    showServiceDetails(response.body());
                } else {
                    Toast.makeText(requireContext(), "Service not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServiceModel> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load service", Toast.LENGTH_SHORT).show();
                Log.e("ServiceDetails", "Error loading service", t);
            }
        });
    }

    private void showServiceDetails(ServiceModel service) {
        nameTextView.setText(service.getName());
        descriptionTextView.setText(service.getDescription());
        priceTextView.setText("Price: " + service.getPrice() + " RSD");
        durationTextView.setText("Duration: " + service.getDurationInMinutes() + " min");
    }
}
