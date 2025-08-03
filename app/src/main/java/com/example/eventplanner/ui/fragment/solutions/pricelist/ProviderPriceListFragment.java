package com.example.eventplanner.ui.fragment.solutions.pricelist;

import static com.example.eventplanner.ui.util.Util.toastError;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.databinding.FragmentProviderPriceListBinding;
import com.example.eventplanner.ui.adapter.PriceListAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProviderPriceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProviderPriceListFragment extends Fragment {
    private FragmentProviderPriceListBinding binding;
    private ProviderPriceListViewModel viewModel;
    private ActivityResultLauncher<Intent> savePdfLauncher;
    byte[] priceListPdf;


    public ProviderPriceListFragment() {
        // Required empty public constructor
    }


    public static ProviderPriceListFragment newInstance() {
        return new ProviderPriceListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        savePdfLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Optional.ofNullable(result.getData())
                                .map(Intent::getData)
                                .ifPresent(this::savePdf);
                    }
                }
        );
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProviderPriceListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProviderPriceListViewModel.class);

        PriceListAdapter adapter = new PriceListAdapter();
        binding.pricelistRecyclerView.setAdapter(adapter);
        binding.pricelistRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter.setOnPriceChangeListener(viewModel::updateSolutionPrice);

        viewModel.solutions.observe(getViewLifecycleOwner(), adapter::setItems);

        viewModel.priceListPdf.observe(getViewLifecycleOwner(), pdf -> {
            priceListPdf = pdf;

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE, "price-list.pdf");
            savePdfLauncher.launch(intent);
        });

        viewModel.errorMsg.observe(getViewLifecycleOwner(), toastError(requireContext()));
        binding.exportPdfButton.setOnClickListener(v -> viewModel.export2Pdf());

        viewModel.fetchProviderSolutions();
    }


    private void savePdf(Uri uri) {
        try (
                OutputStream os = requireContext().getContentResolver().openOutputStream(Objects.requireNonNull(uri))
        ) {
            Objects.requireNonNull(os).write(Objects.requireNonNull(priceListPdf));
            Toast.makeText(requireContext(), "Price list saved successfully", Toast.LENGTH_SHORT).show();
        }
        catch (IOException | NullPointerException e) {
            Toast.makeText(requireContext(), "Failed to save pdf", Toast.LENGTH_SHORT).show();
        }
    }

}