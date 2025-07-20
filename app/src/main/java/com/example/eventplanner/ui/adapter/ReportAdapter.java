package com.example.eventplanner.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.data.model.ReportUserModel;
import java.util.List;
import java.util.function.Consumer;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<ReportUserModel> reportList;
    private Consumer<ReportUserModel> onApproveClick;
    private Consumer<ReportUserModel> onDeleteClick;

    public ReportAdapter(List<ReportUserModel> reportList,
                         Consumer<ReportUserModel> onApproveClick,
                         Consumer<ReportUserModel> onDeleteClick) {
        this.reportList = reportList;
        this.onApproveClick = onApproveClick;
        this.onDeleteClick = onDeleteClick;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportUserModel report = reportList.get(position);

        holder.reportedUserText.setText("Reported User: " + report.getReportedUser());
        holder.reasonText.setText("Reason: " + report.getReason());
        holder.senderText.setText("Blocker: " + report.getSender());
        holder.statusText.setText("Status: " + report.getStatus().toUpperCase());

        holder.approveBtn.setOnClickListener(v -> onApproveClick.accept(report));
        holder.deleteBtn.setOnClickListener(v -> onDeleteClick.accept(report));
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void updateReports(List<ReportUserModel> reports) {
        this.reportList = reports;
        notifyDataSetChanged();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reportedUserText, reasonText, senderText, statusText;
        Button approveBtn, deleteBtn;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reportedUserText = itemView.findViewById(R.id.reportedUserText);
            reasonText = itemView.findViewById(R.id.reasonText);
            senderText = itemView.findViewById(R.id.senderText);
            statusText = itemView.findViewById(R.id.statusText);
            approveBtn = itemView.findViewById(R.id.approveBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
