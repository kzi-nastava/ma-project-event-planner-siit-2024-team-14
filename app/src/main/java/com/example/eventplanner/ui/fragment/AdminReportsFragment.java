package com.example.eventplanner.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.ReportUserModel;
import com.example.eventplanner.data.network.ApiClient;
import com.example.eventplanner.data.network.reports.ReportUserService;
import com.example.eventplanner.ui.adapter.ReportAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReportsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ReportAdapter adapter;
    private ReportUserService reportService;

    public AdminReportsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_reports, container, false);

        recyclerView = view.findViewById(R.id.reportsRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportAdapter(new ArrayList<>(), this::approveReport, this::deleteReport);
        recyclerView.setAdapter(adapter);

        reportService = ApiClient.getClient().create(ReportUserService.class);
        loadReports();
        return view;
    }

    private void loadReports() {
        reportService.getAllReports().enqueue(new Callback<List<ReportUserModel>>() {
            @Override
            public void onResponse(Call<List<ReportUserModel>> call, Response<List<ReportUserModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReportUserModel> reports = response.body();
                    adapter.updateReports(reports);

                    if (reports.isEmpty()) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyTextView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(getContext(), "Failed to load reports", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReportUserModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void approveReport(ReportUserModel report) {
        reportService.approveReportStatus(new ReportUserService.ReportActionBody(report.getReportId(), "accepted"))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getContext(), "Report approved", Toast.LENGTH_SHORT).show();
                        loadReports();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error approving report", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteReport(ReportUserModel report) {
        reportService.deleteReportStatus(new ReportUserService.ReportActionBody(report.getReportId(), "deleted"))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(getContext(), "Report deleted", Toast.LENGTH_SHORT).show();
                        loadReports();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error deleting report", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
