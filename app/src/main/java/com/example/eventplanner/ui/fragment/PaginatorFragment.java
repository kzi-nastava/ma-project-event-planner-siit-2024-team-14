package com.example.eventplanner.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.databinding.FragmentPaginatorBinding;

import java.util.Locale;


public class PaginatorFragment extends Fragment {
    private static final String ARG_PAGE = "page";

    private FragmentPaginatorBinding binding;
    private int page = 0;
    private OnPageChangeListener listener;


    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.listener = listener;
    }



    public PaginatorFragment() {
        // Required empty public constructor
    }


    public static PaginatorFragment newInstance() {
        return newInstance(0);
    }

    public static PaginatorFragment newInstance(int initialPage) {
        PaginatorFragment fragment = new PaginatorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, initialPage);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            page = savedInstanceState.getInt(ARG_PAGE);
        } else if (getArguments() != null) {
            page = getArguments().getInt(ARG_PAGE);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPaginatorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.nextButton.setOnClickListener(v -> {
            ++page;
            updatePageInfo();

            if (listener != null)
                listener.onPageChange(page);
        });

        binding.previousButton.setOnClickListener(v -> {
            if (page >= 1) {
                --page;
                updatePageInfo();

                if (listener != null)
                    listener.onPageChange(page);
            }
        });

        updatePageInfo();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_PAGE, page);
    }


    private void updatePageInfo() {
        binding.pageInfo.setText(String.format(Locale.getDefault(), "Page %d", page));
        binding.previousButton.setEnabled(page > 0);
    }

    @FunctionalInterface
    public interface OnPageChangeListener {
        void onPageChange(int newPage);
    }

}