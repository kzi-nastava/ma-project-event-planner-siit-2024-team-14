package com.example.eventplanner.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.solutions.services.BookingServiceRequestModel;
import com.example.eventplanner.data.network.ApiClient;
import com.example.eventplanner.data.network.services.solutions.BookingServiceRequestService;
import com.example.eventplanner.ui.adapter.AllBookingsAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllBookingsFragment extends Fragment {
    private RecyclerView recyclerView;
    private AllBookingsAdapter adapter;
    private List<BookingServiceRequestModel> bookings = new ArrayList<>();
    private BookingServiceRequestService api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_service_requests, container, false); // možeš napraviti poseban layout ako želiš

        recyclerView = view.findViewById(R.id.booking_requests_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AllBookingsAdapter(bookings);
        recyclerView.setAdapter(adapter);

        api = ApiClient.getClient().create(BookingServiceRequestService.class);
        loadAllBookings();

        return view;
    }

    private void loadAllBookings() {
        api.getAllBookings().enqueue(new Callback<List<BookingServiceRequestModel>>() {
            @Override
            public void onResponse(Call<List<BookingServiceRequestModel>> call, Response<List<BookingServiceRequestModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookings.clear();
                    bookings.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<BookingServiceRequestModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load all bookings", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
