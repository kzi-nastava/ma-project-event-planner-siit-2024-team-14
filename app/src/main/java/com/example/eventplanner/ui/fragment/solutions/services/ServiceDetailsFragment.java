package com.example.eventplanner.ui.fragment.solutions.services;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.data.model.solutions.services.*;
import com.example.eventplanner.R;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.auth.AuthService;
import com.example.eventplanner.databinding.FragmentServiceDetailsBinding;
import com.example.eventplanner.ui.fragment.FragmentTransition;
import com.example.eventplanner.ui.fragment.ServiceReservationFragment;
import com.example.eventplanner.ui.fragment.ViewProviderProfileFragment;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;


import static com.example.eventplanner.data.model.users.UserModel.ROLE_ORGANIZER;
import static com.example.eventplanner.data.model.users.UserModel.ROLE_PROVIDER;


public class ServiceDetailsFragment extends Fragment {
    private static final String ARG_ID = "solutionId", TAG = ServiceDetailsFragment.class.getSimpleName();
    private static final NumberFormat CURRENCY_FMT = NumberFormat.getCurrencyInstance();

    private int serviceId;

    private ServiceViewModel viewModel;
    private FragmentServiceDetailsBinding binding;
    private final AuthService auth = ClientUtils.authService;


    public static ServiceDetailsFragment newInstance(int solutionId) {
        ServiceDetailsFragment fragment = new ServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, solutionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceId = getArguments().getInt(ARG_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentServiceDetailsBinding.inflate(inflater, container, false);
        adjustActions();
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ServiceViewModel.class);

        viewModel.service().observe(getViewLifecycleOwner(), s -> binding.loadingSpinner.setVisibility(View.GONE));
        viewModel.service().observe(getViewLifecycleOwner(), this::showServiceDetails);
        viewModel.service().observe(getViewLifecycleOwner(), this::adjustActions);

        viewModel.errorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null)
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });

        viewModel.fetchService(serviceId);
    }

    private void showServiceDetails(ServiceModel service) {
        binding.serviceName.setText(service.getName());
        binding.serviceDescription.setText(service.getDescription());
        binding.categoryName.setText(service.getCategory().getName());

        binding.servicePrice.setText(CURRENCY_FMT.format(service.getPrice()));
        Optional.ofNullable(service.getDiscount())
                .map(discount -> discount > 1 ? discount / 100 : discount)
                .map(discount -> service.getPrice() * discount)
                .ifPresent(discountedPrice -> {
                    binding.servicePriceDiscounted.setText(
                            String.format("(%s with discount)", CURRENCY_FMT.format(discountedPrice))
                    );
                    binding.servicePriceDiscounted.setVisibility(View.VISIBLE);
                });

        binding.providerCompanyName.setText(service.getProvider().getEmail());

        Optional.ofNullable(service.getDurationMinutes())
                        .ifPresent(duration -> binding.reservationDetails.setText(String.format(Locale.getDefault(), "Duration: %d", duration)));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void adjustActions(ServiceModel service) {
        Optional.ofNullable(auth.getUser())
                .filter(user -> ROLE_PROVIDER.equalsIgnoreCase(user.getRole()))
                .filter(provider -> Objects.equals(provider.getId(), service.getProvider().getId()))
                .ifPresentOrElse(
                        // User is provider that provides this service
                        p -> {
                            binding.editServiceButton.setClickable(true);
                            binding.editServiceButton.setVisibility(View.VISIBLE);
                            binding.editServiceButton.setOnClickListener(view -> {
                                Log.w(TAG, "TODO: Implement Edit Service");
                            });
                        },
                        // Otherwise
                        () -> {
                            binding.providerCompanyName.setClickable(true);
                            binding.providerCompanyName.setOnClickListener(view -> {
                                Bundle args = new Bundle();
                                args.putInt("providerId", service.getProvider().getId());

                                Fragment providerProfileFragment = new ViewProviderProfileFragment();
                                providerProfileFragment.setArguments(args);
                                FragmentTransition.to(providerProfileFragment, requireActivity(), R.layout.fragment_home, true);
                            });

                            SpannableString companyName = new SpannableString(binding.providerCompanyName.getText());
                            companyName.setSpan(new UnderlineSpan(), 0, companyName.length(), 0);
                            binding.providerCompanyName.setText(companyName);
                        }
                );
    }

    private void adjustActions() {
        Optional.ofNullable(auth.getUser())
                .map(u -> u.getRole())
                .ifPresent(
                        role -> {
                            if (ROLE_ORGANIZER.equalsIgnoreCase(role)) {
                                binding.bookServiceButton.setVisibility(View.VISIBLE);
                                binding.bookServiceButton.setOnClickListener(v -> {
                                    Fragment reservationFragment = ServiceReservationFragment.newInstance(serviceId);
                                    FragmentTransition.to(reservationFragment, requireActivity(), R.id.home_page_fragment, true);
                                });

                                binding.chatWithProviderButton.setVisibility(View.VISIBLE);
                                binding.chatWithProviderButton.setOnClickListener(view -> {
                                    Log.w(TAG, "TODO: Implement Chat With Provider");
                                });
                            }
                        }
                );
    }

}
