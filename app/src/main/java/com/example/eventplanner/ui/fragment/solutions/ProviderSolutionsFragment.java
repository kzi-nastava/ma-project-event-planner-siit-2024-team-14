package com.example.eventplanner.ui.fragment.solutions;

import static com.example.eventplanner.data.model.users.UserModel.ROLE_PROVIDER;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.FilterParams;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.databinding.FragmentProviderSolutionsBinding;
import com.example.eventplanner.ui.adapter.SolutionAdapter;
import com.example.eventplanner.ui.fragment.FragmentTransition;
import com.example.eventplanner.ui.fragment.HomeFragment;
import com.example.eventplanner.ui.fragment.PaginatorFragment;
import com.example.eventplanner.ui.fragment.solutions.products.AddProductDialogFragment;
import com.example.eventplanner.ui.fragment.solutions.services.AddServiceFragment;

import java.util.Optional;

public class ProviderSolutionsFragment extends Fragment {
    private FragmentProviderSolutionsBinding binding;
    private SolutionsViewModel viewModel;
    private SolutionAdapter solutionAdapter;

    private FilterParams currentFilters = new FilterParams();

    public static ProviderSolutionsFragment newInstance() {
        return new ProviderSolutionsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Optional.ofNullable(savedInstanceState)
                .map(state -> (FilterParams) state.getSerializable("current_filters"))
                .ifPresent(filters -> currentFilters = filters);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProviderSolutionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!ClientUtils.authService.hasRole(ROLE_PROVIDER)) {
            view.post(() ->
                    FragmentTransition.to(new HomeFragment(), requireActivity(), R.id.home_page_fragment)
            );
            // return; // If you want to hard-block non-providers, uncomment this.
        }

        // Adapter & click -> details
        solutionAdapter = new SolutionAdapter();
        solutionAdapter.setOnSolutionClickedListener(solution ->
                FragmentTransition.to(
                        SolutionDetailsFragment.newInstance(solution),
                        requireActivity(),
                        R.id.home_page_fragment,
                        true
                )
        );
        binding.solutionsRecycler.setAdapter(solutionAdapter);

        // ViewModel + observers
        viewModel = new ViewModelProvider(this).get(SolutionsViewModel.class);
        viewModel.error.observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });
        viewModel.solutions.observe(getViewLifecycleOwner(),
                page -> solutionAdapter.setSolutions(page.getContent()));

        // Filter bar: open filters
        binding.filterBar.toggleFiltersImage.setOnClickListener(v -> {
            SolutionFiltersDialogFragment filtersDialog = SolutionFiltersDialogFragment.newInstance(currentFilters);
            filtersDialog.setOnFilterListener(filters -> {
                currentFilters = filters;
                viewModel.fetchProviderSolutions(currentFilters);
            });
            filtersDialog.show(getParentFragmentManager(), "solution_filters_dialog");
        });

        // Filter bar: search
        binding.filterBar.searchImage.setOnClickListener(v -> {
            String query = binding.filterBar.searchInput.getText().toString();
            currentFilters.setQ(query.isBlank() ? null : query);
            viewModel.fetchProviderSolutions(currentFilters);
        });

        // Paginator
        FragmentManager fm = getChildFragmentManager();
        Fragment paginator = fm.findFragmentById(R.id.paginator);
        if (paginator == null) {
            paginator = PaginatorFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.paginator, paginator)
                    .commit();
        }
        ((PaginatorFragment) paginator).setOnPageChangeListener(this::onPageChanged);

        // Add Service
        binding.addServiceButton.setOnClickListener(v ->
                FragmentTransition.to(
                        AddServiceFragment.newInstance(),
                        requireActivity(),
                        R.id.home_page_fragment,
                        true
                )
        );

        // Listen for Add Product result (refresh on success)
        getChildFragmentManager().setFragmentResultListener(
                "add_product_result",
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    boolean created = bundle.getBoolean("created", false);
                    if (created) {
                        Toast.makeText(requireContext(), "Product created", Toast.LENGTH_SHORT).show();
                        viewModel.fetchProviderSolutions(currentFilters);
                    }
                }
        );

        // Add Product (open dialog)
        binding.addProductButton.setOnClickListener(v -> {
            AddProductDialogFragment dialog = AddProductDialogFragment.newInstance();
            dialog.show(getChildFragmentManager(), "add_product");
        });

        // Initial load
        viewModel.fetchSolutions(currentFilters);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("current_filters", currentFilters);
    }

    private void onPageChanged(int newPage) {
        if (newPage >= 0 && newPage != currentFilters.getPage()) {
            currentFilters.setPage(newPage);
            viewModel.fetchProviderSolutions(currentFilters);
        }
    }
}
