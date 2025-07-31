package com.example.eventplanner.ui.fragment.solutions;

import static com.example.eventplanner.data.model.users.UserModel.ROLE_PROVIDER;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.FilterParams;
import com.example.eventplanner.data.model.users.UserModel;
import com.example.eventplanner.data.network.ClientUtils;
import com.example.eventplanner.databinding.FragmentProviderSolutionsBinding;
import com.example.eventplanner.ui.adapter.SolutionAdapter;
import com.example.eventplanner.ui.fragment.FragmentTransition;
import com.example.eventplanner.ui.fragment.HomeFragment;

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

        String role = Optional.ofNullable(ClientUtils.authService.getUser())
                .map(UserModel::getRole)
                .orElse(null);

        if (!ROLE_PROVIDER.equalsIgnoreCase(role)) {
            view.post(() ->
                FragmentTransition.to(new HomeFragment(), requireActivity(), R.id.home_page_fragment)
            );
            //return;
        }

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

        viewModel = new ViewModelProvider(this).get(SolutionsViewModel.class);

        viewModel.error.observe(getViewLifecycleOwner(), err -> {
            if (err != null)
                Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });

        viewModel.solutions.observe(getViewLifecycleOwner(), page -> solutionAdapter.setSolutions(page.getContent()));

        binding.filterBar.toggleFiltersImage.setOnClickListener(v -> {
            SolutionFiltersDialogFragment filtersDialog = SolutionFiltersDialogFragment.newInstance(currentFilters);

            filtersDialog.setOnFilterListener(filters -> {
                currentFilters = filters;
                viewModel.fetchProviderSolutions(currentFilters);
            });

            filtersDialog.show(getParentFragmentManager(), "solution_filters_dialog");
        });

        binding.filterBar.searchImage.setOnClickListener(v -> {
            String query = binding.filterBar.searchInput.getText().toString();

            currentFilters.setQ( query.isBlank() ? null : query );
            viewModel.fetchProviderSolutions(currentFilters);
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("current_filters", currentFilters);
    }


}
// maybe filters should be moved to the view model :/ ??