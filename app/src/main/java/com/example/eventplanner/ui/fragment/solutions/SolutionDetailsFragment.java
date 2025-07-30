package com.example.eventplanner.ui.fragment.solutions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.OfferingModel;
import com.example.eventplanner.ui.fragment.FragmentTransition;
import com.example.eventplanner.ui.fragment.solutions.products.ProductDetailsFragment;
import com.example.eventplanner.ui.fragment.solutions.services.ServiceDetailsFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SolutionDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SolutionDetailsFragment extends Fragment {
    private static final String ARG_ID = "id", ARG_TYPE = "solutionType";

    private int id;
    private String type;


    public SolutionDetailsFragment() {
        // Required empty public constructor
    }


    public static SolutionDetailsFragment newInstance(OfferingModel solution) {
        return newInstance(solution.getId(), solution.getSolutionType());
    }

    public static SolutionDetailsFragment newInstance(int id, String type) {
        SolutionDetailsFragment fragment = new SolutionDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_ID);
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solution_details, container, false);
        Fragment detailsFragment;

        if ("service".equalsIgnoreCase(type)) {
            detailsFragment = ServiceDetailsFragment.newInstance(id);
        } else if ("product".equalsIgnoreCase(type)) {
            detailsFragment = ProductDetailsFragment.newInstance(id);
        }  else {
            Toast.makeText(requireContext(), "Unknown solution type: " + type, Toast.LENGTH_SHORT).show();
            return view;
        }

        FragmentTransition.to(detailsFragment, requireActivity(), R.id.solution_details_container);
        return view;
    }

}