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
import com.example.eventplanner.data.model.BookingServiceRequestModel;
import com.example.eventplanner.data.network.ApiClient;
import com.example.eventplanner.data.network.services.solutions.BookingServiceRequestService;
import com.example.eventplanner.ui.adapter.BookingServiceRequestAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingServiceRequestFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookingServiceRequestAdapter adapter;
    private List<BookingServiceRequestModel> requestList = new ArrayList<>();
    private BookingServiceRequestService api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_service_requests, container, false);

        recyclerView = view.findViewById(R.id.booking_requests_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        api = ApiClient.getClient().create(BookingServiceRequestService.class);

        adapter = new BookingServiceRequestAdapter(requestList, new BookingServiceRequestAdapter.OnActionClickListener() {
            @Override
            public void onApprove(BookingServiceRequestModel request) {
                Map<String, Object> body = new HashMap<>();
                body.put("requestId", request.getId());
                body.put("approved", "APPROVED");

                api.approveRequest(body).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getContext(), "Approved!", Toast.LENGTH_SHORT).show();
                        loadRequests();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to approve", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onReject(BookingServiceRequestModel request) {
                Map<String, Object> body = new HashMap<>();
                body.put("requestId", request.getId());
                body.put("approved", "REJECTED");

                api.deleteRequest(body).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getContext(), "Rejected!", Toast.LENGTH_SHORT).show();
                        loadRequests();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed to reject", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        recyclerView.setAdapter(adapter);
        loadRequests();

        return view;
    }

    private void loadRequests() {
        api.getAllRequests().enqueue(new Callback<List<BookingServiceRequestModel>>() {
            @Override
            public void onResponse(Call<List<BookingServiceRequestModel>> call, Response<List<BookingServiceRequestModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    requestList.clear();
                    for (BookingServiceRequestModel req : response.body()) {
                        if ("PENDING".equals(req.getConfirmed())) {
                            requestList.add(req);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<BookingServiceRequestModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load requests", Toast.LENGTH_SHORT).show();
            }
        });
    }

}


