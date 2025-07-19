package com.example.eventplanner.ui.fragment.solutions.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.data.model.UserDTO;
import com.example.eventplanner.data.model.solutions.services.ReservationType;
import com.example.eventplanner.data.model.solutions.services.Service;
import com.example.eventplanner.R;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.databinding.FragmentServiceDetailsBinding;
import com.example.eventplanner.ui.fragment.FragmentTransition;
import com.example.eventplanner.ui.fragment.ServiceReservationFragment;

import java.text.NumberFormat;
import java.util.Optional;


public class ServiceDetailsFragment extends Fragment {
    private int serviceId;

    private ServiceViewModel viewModel;
    private FragmentServiceDetailsBinding binding;

    public static ServiceDetailsFragment newInstance(int solutionId) {
        ServiceDetailsFragment fragment = new ServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("solutionId", solutionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceId = getArguments().getInt("solutionId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentServiceDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Optional.ofNullable(ClientUtils.authService.getUser())
                .map(UserDTO::getRole)
                .ifPresent(role -> {
                    if (UserDTO.ROLE_ORGANIZER.equalsIgnoreCase(role)) {
                        binding.bookServiceButton.setVisibility(View.VISIBLE);
                        binding.bookServiceButton.setOnClickListener(v -> {
                            ServiceReservationFragment reservationFragment = ServiceReservationFragment.newInstance(serviceId);
                            FragmentTransition.to(reservationFragment, requireActivity(), R.id.home_page_fragment, true);
                        });
                        binding.chatWithProviderButton.setVisibility(View.VISIBLE);
                    } else if (UserDTO.ROLE_PROVIDER.equalsIgnoreCase(role)) {
                        binding.editServiceButton.setVisibility(View.VISIBLE);
                    }
                });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ServiceViewModel.class);

        viewModel.service().observe(getViewLifecycleOwner(), s -> binding.loadingSpinner.setVisibility(View.GONE));
        viewModel.service().observe(getViewLifecycleOwner(), this::showServiceDetails);

        viewModel.errorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null)
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });

        viewModel.fetchService(serviceId);
    }

    private void showServiceDetails(Service service) {
        binding.serviceName.setText(service.getName());
        binding.serviceDescription.setText(service.getDescription());
        binding.categoryName.setText(service.getCategory().getName());

        NumberFormat currencyFmt = NumberFormat.getCurrencyInstance();
        binding.servicePrice.setText(currencyFmt.format(service.getPrice()));
        Optional.ofNullable(service.getDiscount())
                .map(discount -> discount > 1 ? discount / 100 : discount)
                .map(discount -> service.getPrice() * discount)
                .ifPresent(discountedPrice -> binding.servicePriceDiscounted.setText(String.format("(%s with discount)", currencyFmt.format(discountedPrice))));

        binding.providerEmail.setText(service.getProvider().getEmail());
        binding.providerEmail.setOnClickListener(view -> {
            // TODO: Navigate to provider profile
        });

        binding.reservationDetails.setText(getReservationDetails(service));
    }


    private String getReservationDetails(Service service) {
        StringBuilder sb = new StringBuilder(); // bad :(

// Required advance/cancellation notice
        sb.append("Reservations for this service must be made ")
                .append(service.getReservationPeriodDays())
                .append(" days in advance, and may be canceled up to ")
                .append(service.getCancellationPeriodDays())
                .append(" days before.\n");

// Duration info
        if (service.getDurationMinutes() != null) {
            sb.append("Session duration for this service is ")
                    .append(service.getDurationMinutes())
                    .append(" minutes.\n");
        } else if (service.getMinDurationMinutes() != null && service.getMaxDurationMinutes() != null) {
            sb.append("This service may be reserved for at least ")
                    .append(service.getMinDurationMinutes())
                    .append(" minutes, and up to ")
                    .append(service.getMaxDurationMinutes())
                    .append(" minutes.\n");
        } else {
            sb.append("No duration information available.\n");
        }

// Reservation type info
        if (ReservationType.MANUAL.equals(service.getReservationType())) {
            sb.append("The provider must review reservations before accepting them.");
        } else {
            sb.append("Your reservation will be immediately accepted.");
        }

        return sb.toString();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
