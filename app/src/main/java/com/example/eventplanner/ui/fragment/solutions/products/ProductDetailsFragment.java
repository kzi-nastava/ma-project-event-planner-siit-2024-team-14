package com.example.eventplanner.ui.fragment.solutions.products;

import static com.example.eventplanner.data.model.users.UserModel.ROLE_ORGANIZER;
import static com.example.eventplanner.data.model.users.UserModel.ROLE_PROVIDER;

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

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.products.*;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.data.network.auth.AuthService;
import com.example.eventplanner.databinding.FragmentProductDetailsBinding;
import com.example.eventplanner.ui.fragment.ChatFragment;
import com.example.eventplanner.ui.fragment.FragmentTransition;
import com.example.eventplanner.ui.fragment.ServiceReservationFragment;
import com.example.eventplanner.ui.fragment.ViewProviderProfileFragment;

import java.text.NumberFormat;
import java.util.Objects;
import java.util.Optional;


public class ProductDetailsFragment extends Fragment {
    private static final String ARG_ID = "id", TAG = ProductDetailsFragment.class.getSimpleName();
    private static final NumberFormat CURRENCY_FMT = NumberFormat.getCurrencyInstance();

    private int id;

    private FragmentProductDetailsBinding binding;
    private ProductViewModel viewModel;
    private final AuthService auth = ClientUtils.authService;


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
        id = requireArguments().getInt(ARG_ID);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        adjustActions();
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
        viewModel.product().observe(getViewLifecycleOwner(), p -> binding.loadingSpinner.setVisibility(View.GONE));
        viewModel.product().observe(getViewLifecycleOwner(), this::adjustActions);

        viewModel.fetchProduct(id);
    }


    private void showProductDetails(ProductModel product) {
        binding.productName.setText(product.getName());
        binding.productDescription.setText(product.getDescription());

        binding.productPrice.setText(CURRENCY_FMT.format(product.getPrice()));
        Optional.ofNullable(product.getDiscount())
                .map(discount -> discount > 1 ? discount / 100 : discount)
                .map(discount -> product.getPrice() * discount)
                .ifPresent(discountedPrice -> {
                    binding.productPriceDiscounted.setText(
                            String.format("(%s with discount)", CURRENCY_FMT.format(discountedPrice))
                    );
                    binding.productPriceDiscounted.setVisibility(View.VISIBLE);
                });

        binding.providerCompanyName.setText(Optional.ofNullable(product.getProvider()).map(p -> p.getCompanyName()).orElse("Provider Company Name"));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void adjustActions(ProductModel product) {

        // is provider looking at his own product
        if (auth.hasRole(ROLE_PROVIDER) && Objects.equals(auth.getUser().getId(), product.getProvider().getId())) {
            binding.editProductButton.setClickable(true);
            binding.editProductButton.setVisibility(View.VISIBLE);
            binding.editProductButton.setOnClickListener(view -> Log.w(TAG, "TODO: Navigate to edit product"));
            return;
        }

        // allow organizers to contact provider
        if (auth.hasRole(ROLE_ORGANIZER)) {
            binding.chatWithProviderButton.setVisibility(View.VISIBLE);
            binding.chatWithProviderButton.setOnClickListener(view ->
                    FragmentTransition.to(
                            ChatFragment.newInstance(product.getProvider().getId()),
                            requireActivity(),
                            R.id.home_page_fragment,
                            true
                    )
            );
        }

        // link to provider profile
        binding.providerCompanyName.setClickable(true);
        binding.providerCompanyName.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putInt("providerId", product.getProvider().getId());

            Fragment providerProfileFragment = new ViewProviderProfileFragment();
            providerProfileFragment.setArguments(args);
            FragmentTransition.to(providerProfileFragment, requireActivity(), R.layout.fragment_home, true);
        });

        SpannableString companyName = new SpannableString(binding.providerCompanyName.getText());
        companyName.setSpan(new UnderlineSpan(), 0, companyName.length(), 0);
        binding.providerCompanyName.setText(companyName);
    }

    private void adjustActions() {
        if (auth.hasRole(ROLE_ORGANIZER)) {
            binding.purchaseProductButton.setVisibility(View.VISIBLE);
            binding.purchaseProductButton.setOnClickListener(v -> {
                Fragment reservationFragment = ServiceReservationFragment.newInstance(id);
                FragmentTransition.to(reservationFragment, requireActivity(), R.id.home_page_fragment, true);
            });
        }
    }

}
