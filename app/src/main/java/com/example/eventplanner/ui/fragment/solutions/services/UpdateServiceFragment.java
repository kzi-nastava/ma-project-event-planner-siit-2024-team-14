package com.example.eventplanner.ui.fragment.solutions.services;

import static com.example.eventplanner.data.model.users.UserModel.ROLE_PROVIDER;
import static com.example.eventplanner.ui.util.SimpleTextWatcher.AfterTextChanged;
import static com.example.eventplanner.ui.util.Util.parseDouble;
import static com.example.eventplanner.ui.util.Util.parseInt;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.Category;
import com.example.eventplanner.data.model.solutions.services.ReservationType;
import com.example.eventplanner.data.model.solutions.services.ServiceModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.auth.AuthService;
import com.example.eventplanner.databinding.FragmentUpdateServiceBinding;
import com.example.eventplanner.databinding.ViewServiceFormBinding;
import com.example.eventplanner.ui.fragment.FragmentTransition;
import com.example.eventplanner.ui.fragment.HomeFragment;
import com.example.eventplanner.ui.fragment.solutions.ProviderSolutionsFragment;

import java.util.Objects;
import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateServiceFragment extends Fragment {
    private static final String ARG_ID = "s e r v i c e";

    private int serviceId;
    private ViewServiceFormBinding form;
    private ServiceViewModel viewModel;
    private final AuthService authService = ClientUtils.authService;


    public UpdateServiceFragment() {
        // Required empty public constructor
    }


    public static AddServiceFragment newInstance(int serviceId) {
        AddServiceFragment fragment = new AddServiceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, serviceId);
        fragment.setArguments(args);
        return fragment;
    }


    public static AddServiceFragment newInstance(ServiceModel service) {
        return newInstance(Objects.requireNonNull(service).getId());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serviceId = requireArguments().getInt(ARG_ID);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentUpdateServiceBinding binding = FragmentUpdateServiceBinding.inflate(inflater, container, false);

        form = binding.form;
        form.submitButton.setText(R.string.save);
        form.categorySpinnerContainer.setVisibility(View.GONE);
        form.deleteButton.setVisibility(View.VISIBLE);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!authService.hasRole(ROLE_PROVIDER)) {
            view.post(() ->
                    FragmentTransition.to(new HomeFragment(), requireActivity(), R.id.home_page_fragment)
            );

            return;
        }

        // one view model per service in context of host activity
        ServiceViewModelFactory factory = new ServiceViewModelFactory(serviceId);
        viewModel = new ViewModelProvider(requireActivity(), factory).get(factory.getKey(), ServiceViewModel.class);

        viewModel.error().observe(getViewLifecycleOwner(), this::toastError);
        viewModel.service().observe(getViewLifecycleOwner(), service -> {
            if (service == null) { // when deleted emits null
                FragmentTransition.to(
                        ProviderSolutionsFragment.newInstance(),
                        requireActivity(),
                        R.id.home_page_fragment
                );

                return;
            }

            populateServiceData(service);
        });

        setupInputListeners();
        viewModel.fetchService(); //
    }


    @SuppressWarnings("OptionalOfNullableMisuse")
    private void populateServiceData(ServiceModel service) {
        if (service == null)
            return;

        Optional.ofNullable(service.getName())
                .ifPresent(form.nameInput::setText);

        Optional.ofNullable(service.getDescription())
                .ifPresent(form.descriptionInput::setText);

        Optional.ofNullable(service.getPrice())
                .map(String::valueOf)
                .ifPresent(form.priceInput::setText);

        Optional.ofNullable(service.getDiscount())
                .map(String::valueOf)
                .ifPresent(form.discountInput::setText);

        Optional.ofNullable(service.getDurationMinutes())
                .map(String::valueOf)
                .ifPresent(form.durationInput::setText);

        Optional.ofNullable(service.getReservationPeriodDays())
                .map(String::valueOf)
                .ifPresent(form.reservationPeriodInput::setText);

        Optional.ofNullable(service.getCancellationPeriodDays())
                .map(String::valueOf)
                .ifPresent(form.cancellationPeriodInput::setText);

        Optional.ofNullable(service.getReservationType())
                .map(ReservationType.AUTOMATIC.toString()::equals)
                .ifPresent(form.autoAcceptCbx::setChecked);
    }

    private void setupInputListeners() {
        form.nameInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    String name = editable.toString().trim();
                    viewModel.updateService(ServiceModel::setName, name);
                })
        );

        form.descriptionInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    String description = editable.toString().trim();
                    viewModel.updateService(ServiceModel::setDescription, description);
                })
        );

        form.priceInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    double price = parseDouble(editable.toString().trim(), -1);
                    viewModel.updateService(ServiceModel::setPrice, price);
                })
        );

        form.discountInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    double discount = parseDouble(editable.toString().trim(), 0);
                    viewModel.updateService(ServiceModel::setDiscount, discount);
                })
        );

        form.categoryNameInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    String name = editable.toString().trim();

                    viewModel.updateService(
                            (service, value) -> {
                                Category category;

                                if ((category = service.getCategory()) == null)
                                    service.setCategory(category = new Category());

                                category.setName(value);
                            },
                            name
                    );
                })
        );

        form.categoryDescriptionInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    String description = editable.toString().trim();

                    viewModel.updateService(
                            (service, value) -> {
                                Category category;

                                if ((category = service.getCategory()) == null)
                                    service.setCategory(category = new Category());

                                category.setDescription(value);
                            },
                            description
                    );
                })
        );

        form.durationInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    int durationMinutes = parseInt(editable.toString().trim(), -1);
                    viewModel.updateService(ServiceModel::setDurationMinutes, durationMinutes);
                })
        );

        form.reservationPeriodInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    int reservationPeriodDays = parseInt(editable.toString().trim(), 0);
                    viewModel.updateService(ServiceModel::setReservationPeriodDays, reservationPeriodDays);
                })
        );

        form.cancellationPeriodInput.addTextChangedListener(
                AfterTextChanged(editable -> {
                    int cancellationPeriodDays = parseInt(editable.toString().trim(), 0);
                    viewModel.updateService(ServiceModel::setCancellationPeriodDays, cancellationPeriodDays);
                })
        );

        form.autoAcceptCbx.setOnCheckedChangeListener((buttonView, isChecked) ->
                viewModel.updateService(ServiceModel::setReservationType, isChecked ? ReservationType.AUTOMATIC.toString() : ReservationType.MANUAL.toString())
        );

        form.deleteButton.setOnClickListener(v -> this.onDeleteService());
        form.submitButton.setOnClickListener(v -> viewModel.updateService());
    }

    private void onDeleteService() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_confirmation_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> viewModel.deleteService())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    private void toastError(String err) {
        if (err != null)
            Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
    }

}